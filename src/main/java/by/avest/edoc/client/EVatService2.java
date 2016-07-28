//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package by.avest.edoc.client;

import by.avest.crypto.cert.verify.CertVerify;
import by.avest.edoc.client.AvDoc;
import by.avest.edoc.client.AvDocException;
import by.avest.edoc.client.AvEDoc;
import by.avest.edoc.client.AvEList;
import by.avest.edoc.client.AvEStatus;
import by.avest.edoc.client.AvETicket;
import by.avest.edoc.client.CertStoreBuilderParams;
import by.avest.edoc.client.InvoicesIntf;
import by.avest.edoc.client.InvoicesService;
import by.avest.edoc.client.PersonalKeyManager;
import by.avest.edoc.client.XmlUtil;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.util.Date;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.SOAPBinding;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EVatService2 {
    private String wsdlLocation;
    private InvoicesIntf port;
    private PersonalKeyManager keyManager;
    private String alias;
    private PKIXBuilderParameters builderParams;
    private TrustManager[] trustManagers;

    private static String fixWSDLLocation(String wsdlLocation) {
        return wsdlLocation != null && !wsdlLocation.endsWith("?wsdl")?wsdlLocation + "?wsdl":wsdlLocation;
    }

    public EVatService2(String wsdlLocation, PersonalKeyManager keyManager) throws CertificateException, KeyStoreException, IOException, AvDocException {
        this.wsdlLocation = fixWSDLLocation(wsdlLocation);
        this.builderParams = CertStoreBuilderParams.getBuilderParams();
        this.keyManager = keyManager;
        this.keyManager.setCertVerify(new CertVerify(this.builderParams, true));
        this.trustManagers = CertStoreBuilderParams.getTrustManagers(this.builderParams);
    }

    public AvEDoc createEDoc() {
        PrivateKey key = this.getMngrKey();
        X509Certificate cert = this.getMngrCert();
        AvEDoc result = new AvEDoc(key, cert, new CertVerify(this.builderParams, true));
        return result;
    }

    private PrivateKey getMngrKey() {
        return this.keyManager.getPrivateKey(this.alias);
    }

    private X509Certificate getMngrCert() {
        X509Certificate result = null;
        X509Certificate[] chain = this.keyManager.getCertificateChain(this.alias);
        if(chain != null && chain.length != 0) {
            result = chain[0];
        }

        return result;
    }

    public void login() throws AvDocException {
        this.alias = this.keyManager.chooseClientAlias(new String[0], (Principal[])null, (Socket)null);
        this.keyManager.getPrivateKey(this.alias);
        this.keyManager.getCertificateChain(this.alias);
    }

    public void login(String str) throws AvDocException {
        if(str != null && !str.isEmpty()) {
            this.keyManager.init(str);
            this.alias = this.keyManager.chooseClientAlias(new String[0], (Principal[])null, (Socket)null);
            this.keyManager.getPrivateKey(this.alias);
            this.keyManager.getCertificateChain(this.alias);
        } else {
            this.login();
        }

    }

    public void connect() throws KeyManagementException, InvalidAlgorithmParameterException, NoSuchAlgorithmException, KeyStoreException, CertificateException, IOException, AvDocException {
        this.port = this.openServicePort(this.wsdlLocation);
    }

    private InvoicesIntf openServicePort(String wsdlLocation) throws KeyStoreException, CertificateException, IOException, KeyManagementException, AvDocException {
        InvoicesIntf result = null;
        SSLContext context = null;

        try {
            context = SSLContext.getInstance("AvTLS");
        } catch (NoSuchAlgorithmException var12) {
            throw new AvDocException("Неверный алгоритм SSL протокола.", var12);
        }

        context.init(new KeyManager[]{this.keyManager}, this.trustManagers, (SecureRandom)null);
        SSLSocketFactory sf = context.getSocketFactory();
        HttpsURLConnection.setDefaultSSLSocketFactory(sf);
        URL wsdlURL = new URL(wsdlLocation);
        InvoicesService ss = new InvoicesService(wsdlURL, InvoicesService.SERVICE);
        result = ss.getInvoicesPort();
        Client client = ClientProxy.getClient(result);
        HTTPConduit condoit = (HTTPConduit)client.getConduit();
        TLSClientParameters params = new TLSClientParameters();
        params.setSSLSocketFactory(sf);
        params.setDisableCNCheck(true);
        condoit.setTlsClientParameters(params);
        BindingProvider bindingProvider = (BindingProvider)result;
        SOAPBinding sopadBinding = (SOAPBinding)bindingProvider.getBinding();
        sopadBinding.setMTOMEnabled(true);
        return result;
    }

    public AvETicket sendEDoc(AvEDoc eDoc) throws IOException, AvDocException, ParseException {
        AvDoc document = eDoc.getDocument();
        String generalNumber = document.getXmlNodeValue("/issuance/general/number");
        if(generalNumber == null) {
            throw new AvDocException("Документ не содержит элемент \'/issuance/general/number\'.");
        } else {
            Object response = null;
            byte[] response1;
            if(eDoc.getSignCount() == 1) {
                response1 = this.port.put(eDoc.getEncoded());
            } else {
                response1 = this.port.putFinal(eDoc.getEncoded());
            }

            AvETicket ticket = new AvETicket(new CertVerify(this.builderParams, true));
            ticket.setOidValue(generalNumber);
            ticket.load(response1);
            return ticket;
        }
    }

    public AvEList getList(Date date) throws IOException, AvDocException, ParseException {
        Document selector = createSelectorByDate(XmlUtil.date2String(date));
        byte[] list = this.port.list(XmlUtil.xml2ByteArray(selector));
        AvEList result = new AvEList(new CertVerify(this.builderParams, true));
        result.load(list);
        return result;
    }

    public AvEStatus getStatus(String invNum) throws AvDocException, IOException, ParseException {
        Document selector = createSelectorByInvNum(invNum);
        byte[] list = this.port.status(XmlUtil.xml2ByteArray(selector));
        AvEStatus result = new AvEStatus(new CertVerify(this.builderParams, true));
        result.load(list);
        return result;
    }

    public AvEDoc getEDoc(String invNum) throws IOException, AvDocException, ParseException {
        Document selector = createSelectorByInvNum(invNum);
        byte[] invoice = this.port.get(XmlUtil.xml2ByteArray(selector));
        PrivateKey key = this.getMngrKey();
        X509Certificate cert = this.getMngrCert();
        AvEDoc result = new AvEDoc(key, cert, new CertVerify(this.builderParams, true));
        result.load(invoice);
        return result;
    }

    private static Document createSelectorByDate(String startDate) throws AvDocException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;

        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException var7) {
            throw new AvDocException("Не удалось создать объект XML данных.", var7);
        }

        Document doc = docBuilder.newDocument();
        Element selectors = doc.createElement("selectors");
        doc.appendChild(selectors);
        Element selector = doc.createElement("selector");
        Attr type = doc.createAttribute("type");
        type.setValue("issuance:billed:from_date");
        selector.setAttributeNode(type);
        selector.setTextContent(startDate);
        selectors.appendChild(selector);
        return doc;
    }

    private static Document createSelectorByInvNum(String invnumber) throws AvDocException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;

        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException var7) {
            throw new AvDocException("Не удалось создать объект XML данных.", var7);
        }

        Document doc = docBuilder.newDocument();
        Element selectors = doc.createElement("selectors");
        doc.appendChild(selectors);
        Element selector = doc.createElement("selector");
        Attr type = doc.createAttribute("type");
        type.setValue("issuance:number");
        selector.setAttributeNode(type);
        selector.setTextContent(invnumber);
        selectors.appendChild(selector);
        return doc;
    }

    public void disconnect() throws IOException {
    }

    public void logout() {
        this.keyManager.reset();
    }
}
