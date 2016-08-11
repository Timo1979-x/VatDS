package by.gto.tools;

import by.avest.certstore.AvCertStoreProvider;
import by.avest.crypto.ocsp.client.protocol.util.Base64;
import by.avest.crypto.pkcs11.provider.AvestProvider;
import by.avest.crypto.pkcs11.provider.ProviderFactory;
import by.avest.edoc.client.*;
import by.avest.net.tls.AvTLSProvider;
import by.gto.btoreport.gui.Main;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.ws.soap.SOAPFaultException;

/**
 * Created by Tim on 23.07.2016.
 */
public class VatTool implements Closeable {
    private final static Logger log = Logger.getLogger(VatTool.class);
    private static byte[] xsdOriginal;
    private static byte[] xsdAdditional;
    private static byte[] xsdFixed;
    private EVatService2 service;
    //private static String xsddirname = VatTool.class.getClassLoader().getResource("xsd").getPath();
    private static File outdir = new File(ConfigReader.getInstance().getVatPath() + "\\out");
    private boolean delete = false;

    private static AvestProvider avestProvider;
    private static AvTLSProvider avTlsProvider;
    private static AvCertStoreProvider avCertStoreProvider;
    private long numberBegin;
    private long numberEnd;
    private List<Long> numbersUsed = new ArrayList<>();

    public VatTool() throws UnrecoverableKeyException, CertificateException,
            NoSuchAlgorithmException, KeyStoreException, IOException, AvDocException,
            InvalidAlgorithmParameterException, KeyManagementException {
        AvestProvider avProv = null;
        AvTLSProvider tlsProv = null;
        AvCertStoreProvider storeProv = null;

        //final Provider[] providers = Security.getProviders();
        avProv = getAvestProvider();
        tlsProv = getAvTlsProvider();
        storeProv = getAvCertStoreProvider();
        //final Provider[] providers1 = Security.getProviders();


        String wsdlUrlString = getVatServiceUrl();
        URL wsdlUrl = new URL(wsdlUrlString);
        String refUrlString = String.format("https://%s:%d/cxf/dictionary/grp/%%s?s=5", wsdlUrl.getHost(), wsdlUrl.getPort());
        this.service = new EVatService2(wsdlUrlString, refUrlString, new KeySelector());
        this.service.login("");
        log.info("[OK] Авторизация успешна");
        this.printConnectionInfo();
        this.service.connect();
        log.info("[OK] Подключение успешно");
    }

    private String getVatServiceUrl() {
        String url = System.getProperty("by.gto.btoreport.avest.url");
        if (url == null) {
            url = ConfigReader.getInstance().getVatServiceUrl();
        }
        return url;
    }

    private static AvestProvider getAvestProvider() {
        if (avestProvider == null) {
            avestProvider = ProviderFactory.addAvUniversalProvider();
        }
        return avestProvider;
    }

    private static AvTLSProvider getAvTlsProvider() {
        if (avTlsProvider == null) {
            avTlsProvider = new AvTLSProvider();
            Security.addProvider(avTlsProvider);
        }
        return avTlsProvider;
    }

    private static AvCertStoreProvider getAvCertStoreProvider() {
        if (avCertStoreProvider == null) {
            avCertStoreProvider = new AvCertStoreProvider();
            Security.addProvider(avCertStoreProvider);
        }
        return avCertStoreProvider;
    }

//    public void doSignAndUploadStrings(Map<String, String> xmlInfo, Callback1 callback) throws Exception {
//        for (Map.Entry<String, String> entry : xmlInfo.entrySet()) {
//            try {
//                System.out.println("[OK] Обработка ЭСЧФ \'" + entry.getKey());
//                callback.call(entry.getKey() + " загружается.");
//                this.doSignAndUploadString(entry.getValue());
//            } catch (Exception e) {
//                throw new Exception("[ОШИБКА] Не удалось обработать ЭСЧФ " + entry.getKey() + ".\n" + e.getMessage());
//            }
//        }
//
//        this.service.disconnect();
//        this.service.logout();
//    }

    private void printConnectionInfo() {
        String host = System.getProperty("https.proxyHost");
        String port = System.getProperty("https.proxyPort");
        StringBuilder sb = new StringBuilder();
        sb.append("[OK] Подключение к ");
        sb.append(getVatServiceUrl());
        if (host != null) {
            sb.append(" через прокси ");
            sb.append(host);
            if (port != null) {
                sb.append(":");
                sb.append(port);
            }
        }

        log.info(sb.toString());
    }



    /**
     * @param number номер проверяемой счет-фактуры
     * @return 0 - с/ф не найдена (статус NOT_FOUND);
     * 1- с/ф найдена на портале, но еще может быть удалена (статусы IN_PROGRESS или IN_PROGRESS_ERROR);
     * 2 - с/ф найдена и уже останется на портале навечно (все остальные статусы)
     * 3- ошибка получения статуса
     */

    public byte isNumberSpare(String number) throws Exception {
        AvEStatus status = null;
        try {
            status = this.service.getStatus(number);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return 3;
        }
        if ("DENIED".equals(status.getStatus())) {
            throw new Exception(status.getMessage());
        }
        if ("NOT_FOUND".equals(status.getStatus())) return 0;
        //if ("IN_PROGRESS".equals(status.getStatus()) || "IN_PROGRESS_ERROR".equals(status.getStatus())) return 1;
        if ("COMPLETED".equals(status.getStatus())
                || "COMPLETED_SIGNED".equals(status.getStatus())
                || "ON_AGREEMENT".equals(status.getStatus())
                || "CANCELLED".equals(status.getStatus())
                || "ON_AGREEMENT_CANCEL".equals(status.getStatus())) {
            return 2;
        }
        return 1;
    }

    public void doSignAndUploadString(String inXml) throws Exception {
        AvEDoc eDoc = this.service.createEDoc();

        final byte[] docInMemory = inXml.getBytes("UTF-8"); //readFile(infile);
        eDoc.getDocument().load(docInMemory);
        String invoicenum = eDoc.getDocument().getXmlNodeValue("issuance/general/number");
        final byte status = isNumberSpare(invoicenum);
        if (status != 0) {
            return;
            //throw new Exception(String.format("Номер %s уже занят!", invoicenum));
        }

        String unp = eDoc.getDocument().getXmlNodeValue("issuance/recipient/unp");
        this.service.checkUNP(unp);

        String docType = eDoc.getDocument().getXmlNodeValue("issuance/general/documentType");
        log.info("[OK] Документ  \'" + invoicenum + "\', тип документа \'" + docType + "\'.");
        byte[] xsdSchema = loadXsdSchema(/*xsddirname,*/ docType);
        boolean isDocumentValid = eDoc.getDocument().validateXML(xsdSchema);
        if (!isDocumentValid) {
            throw new Exception("Cтруктура документа не соответствует XSD схеме.");
        } else {
            eDoc.sign();
            log.info("[OK] Документ подписан.");
            byte[] signedDocument = eDoc.getEncoded();
            File outdir = this.getOutDir();
            File outSigned = new File(outdir, "invoice-" + invoicenum + ".sgn.xml");
            writeFile(outSigned, signedDocument);
            AvETicket ticket = null;

            try {
                ticket = this.service.sendEDoc(eDoc);
                log.info("[OK] Документ отправлен.");
            } catch (SOAPFaultException ex) {
                log.error("[ОШИБКА] При обработке запроса на сервере произошла ошибка: " + ex.getMessage());
                throw ex;
                //System.exit(1);
            }

            File outTicket;
            if (ticket.accepted()) {
                String msg = ticket.getMessage();
                outTicket = new File(outdir, "invoice-" + invoicenum + ".ticket.xml");
                writeFile(outTicket, ticket.getEncoded());
                log.info("[OK] Счет-фактура НДС с номером \'" + invoicenum + "\' принят к обработке. Сервер вернул ответ: " + msg);
            } else {
                AvError err1 = ticket.getLastError();
                outTicket = new File(outdir, "invoice-" + invoicenum + ".ticket.error.xml");
                writeFile(outTicket, ticket.getEncoded());
                log.error("[ОШИБКА] Не удалось загрузить счет-фактуру НДС с номером \'" + invoicenum + "\'. " + err1.getMessage());
            }

        }
    }

//    private void doSignFile(File infile) throws Exception {
//        System.out.println("[OK] Обработка файла \'" + infile.getAbsolutePath() + "\'.");
//        AvEDoc eDoc = this.service.createEDoc();
//        eDoc.getDocument().load(readFile(infile));
//        String invoicenum = eDoc.getDocument().getXmlNodeValue("issuance/general/number");
//        String docType = eDoc.getDocument().getXmlNodeValue("issuance/general/documentType");
//        System.out.println("[OK] Документ  \'" + invoicenum + "\', тип документа \'" + docType + "\'.");
//        byte[] xsdSchema = loadXsdSchema(this.xsddirname, docType);
//        boolean isDocumentValid = eDoc.getDocument().validateXML(xsdSchema);
//        if (!isDocumentValid) {
//            throw new Exception("Cтруктура документа не соответствует XSD схеме.");
//        } else {
//            eDoc.sign();
//            System.out.println("[OK] Документ подписан.");
//            byte[] signedDocument = eDoc.getEncoded();
//            File outdir = this.getOutDir();
//            File outSigned = new File(outdir, "invoice-" + invoicenum + ".sgn.xml");
//            writeFile(outSigned, signedDocument);
//            System.out.println("[OK] Счет-фактура НДС с номером \'" + invoicenum + "\' подписан и сохранен в файл \'" + outSigned.getAbsolutePath() + "\'.");
//        }
//    }

    private File getOutDir() throws Exception {
        outdir.mkdirs();
        return outdir;
    }

    private static void writeFile(File file, byte[] data) throws IOException {
        DataOutputStream os = new DataOutputStream(new FileOutputStream(file));
        os.write(data);
        os.close();
    }

    private static byte[] readResource(String relpath) throws IOException {
        byte buf[] = new byte[5 * 1024];
        final URL resource = VatTool.class.getResource(relpath);
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (InputStream iss = resource.openStream()) {
                while (true) {
                    int r = iss.read(buf);
                    if (r <= 0) break;
                    baos.write(buf, 0, r);
                }
            }
            return baos.toByteArray();
        }
    }

//    private static byte[] readFile(File file) throws FileNotFoundException, IOException {
//        byte[] fileData = new byte[(int) file.length()];
//        DataInputStream dis = new DataInputStream(new FileInputStream(file));
//
//        try {
//            dis.readFully(fileData);
//        } finally {
//            dis.close();
//        }
//
//        return fileData;
//    }

    private static byte[] loadXsdSchema(/*String xsdFolderName,*/ String doctype) throws Exception {
        File xsdFile = null;
        doctype = doctype == null ? "" : doctype;
        if ("ORIGINAL".equalsIgnoreCase(doctype) || "ADD_NO_REFERENCE".equalsIgnoreCase(doctype)) {
            if (xsdOriginal == null) {
                //xsdOriginal = readFile(new File(xsdFolderName, "MNSATI_original.xsd"));
                xsdOriginal = readResource("/xsd/MNSATI_original.xsd");
            }
            return xsdOriginal;
        } else if ("FIXED".equalsIgnoreCase(doctype)) {
            if (xsdFixed == null) {
                //xsdFixed = readFile(new File(xsdFolderName, "MNSATI_fixed.xsd"));
                xsdFixed = readResource("/xsd/MNSATI_fixed.xsd");
            }
            return xsdFixed;
        } else if (doctype.equalsIgnoreCase("ADDITIONAL")) {
            if (xsdAdditional == null) {
                //xsdAdditional = readFile(new File(xsdFolderName, "MNSATI_additional.xsd"));
                xsdAdditional = readResource("/xsd/MNSATI_additional.xsd");
            }
            return xsdAdditional;
        } else {
            throw new Exception("Неизвестный тип счета-фактуры НДС \'" + doctype + "\'.");
        }
    }

    @Override
    public void close() throws IOException {
        if (this.service != null) {
            this.service.disconnect();
            this.service.logout();
        }
    }
}
