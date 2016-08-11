package by.gto.tools;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.*;

/**
 * @author Aleks
 */
public class ConfigReader {

    private static final Logger log = LogManager.getLogger(ConfigReader.class);
    private static volatile ConfigReader instance;
    private String host = "127.0.0.1";
    private String port = "33060";
    private String chiefDS = "ФИО";
    private String position = "Должность";
    private int NDS = 20;
    private int UNP = 0;
    private String orgName = "Организация";
    private String serviceName = "Контрольно-диагностические работы";
    //private File configXml = new File("config.xml");
    private String vatServiceUrl = "https://ws.vat.gov.by:443/InvoicesWS/services/InvoicesPort?wsdl";

    public  String getVatServiceUrl() {
        return vatServiceUrl;
    }

    public  void setVatServiceUrl(String vatServiceUrl) {
        this.vatServiceUrl = vatServiceUrl;
    }

    public String getVatPath() {
        return vatPath;
    }

    public void setVatPath(String vatPath) {
        this.vatPath = vatPath;
    }

    private String vatPath = "c:\\";

    //тестовый - https://185.32.226.170:4443/InvoicesWS/services/InvoicesPort?wsdl

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public int getUNP() {
        return UNP;
    }

    public void setUNP(int UNP) {
        this.UNP = UNP;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    //private static final File configXml = new File("config.xml");
    private static final File configXml = new File(System.getenv("APPDATA") + "\\btoReportNG\\config.xml");

    /**
     * @return
     */
    public static ConfigReader getInstance() {
        if (instance == null) {
            synchronized (ConfigReader.class) {
                if (instance == null) {
                    instance = new ConfigReader();
                }
            }
        }
        return instance;
    }

    private XMLConfiguration config;

    private ConfigReader() {

        if (!configXml.exists()) {
            _save(configXml);
        }
        try {
            config = new XMLConfiguration(configXml);
        } catch (ConfigurationException ex) {
            log.error(ex);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка!", JOptionPane.ERROR_MESSAGE);
        }
        read();
    }

    private void _save(File configXml) {
        configXml.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(configXml);
             OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF8");
             Writer wFile = new BufferedWriter(osw)) {

            wFile.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
            wFile.write("<configuration>\n");
            wFile.write("    <config>\n");
            wFile.write(String.format("        <host>%s</host>\n", getHost()));
            wFile.write(String.format("        <port>%s</port>\n", getPort()));
            wFile.write(String.format("        <position>%s</position>\n", getPosition()));
            wFile.write(String.format("        <chiefDS>%s</chiefDS>\n", getChiefDS()));
            wFile.write(String.format("        <NDS>%s</NDS>\n", getNDS()));
            wFile.write(String.format("        <UNP>%s</UNP>\n", getUNP()));
            wFile.write(String.format("        <orgName>%s</orgName>\n", getOrgName()));
            wFile.write(String.format("        <serviceName>%s</serviceName>\n", getServiceName()));
            wFile.write(String.format("        <vatServiceUrl>%s</vatServiceUrl>\n", getVatServiceUrl()));
            wFile.write(String.format("        <vatPath>%s</vatPath>\n", getVatPath()));
            wFile.write("    </config>\n");
            wFile.write("</configuration>\n");
            wFile.flush();
        } catch (Exception ex) {
            log.error(ex);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка!", JOptionPane.ERROR_MESSAGE);
        }
    }
//    public void ConfigReader() {
//        if (configXml.exists()) {
//            try {
//                config = new XMLConfiguration("config.xml");
////            config.addProperty("tables.table.fields.field(-1).name", "id");
////            config.addProperty("tables.table.fields.field.type", "int");
////            config.setProperty("colors.background", "#000000");
////            config.save();
////            config.setProperty("config.host", "192.168.0.1");
////           config.save();
////                config.load();
////                host = config.getString("config.host").trim();
////                port = config.getString("config.port").trim();
////                chiefDS = config.getString("config.chiefDS").trim();
//                //     host = config.getKeys("host").toString();
//            } catch (ConfigurationException ex) {
//                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } else {
//            try {
//                try (Writer wFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configXml), "UTF8"))) {
//                    wFile.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
//                    wFile.write("<configuration>\n");
//                    wFile.write("    <config>\n");
//                    wFile.write("        <host>" + host + "</host>\n");
//                    wFile.write("        <port>" + port + "</port>\n");
//                    wFile.write("        <chiefDS>" + chiefDS + "</chiefDS>\n");
//                    wFile.write("    </config>\n");
//                    wFile.write("</configuration>\n");
//                    wFile.flush();
//                }
//            } catch (IOException ex) {
//                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        read();
//    }
//

    private void read() {
        try {
            config.load();
            host = "localhost".equals(config.getString("config.host", host).trim()) ? "127.0.0.1" : config.getString("config.host", host).trim();
            port = config.getString("config.port", port).trim();
            position = config.getString("config.position", position).trim();
            chiefDS = config.getString("config.chiefDS", chiefDS).trim();
            NDS = config.getInt("config.NDS", NDS);
            UNP = config.getInt("config.UNP", UNP);
            orgName = config.getString("config.orgName", orgName).trim();
            serviceName = config.getString("config.serviceName", serviceName).trim();
            vatServiceUrl = config.getString("config.vatServiceUrl", vatServiceUrl).trim();
            vatPath = config.getString("config.vatPath", vatPath).trim();

        } catch (Exception ex) {
            log.error(ex.getMessage());
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Ошибка!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getChiefDS() {
        return chiefDS;
    }

    public int getNDS() {
        return NDS;
    }

    public String getPosition() {
        return position;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setChiefDS(String chiefDS) {
        this.chiefDS = chiefDS;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setNDS(int NDS) {
        this.NDS = NDS;
    }

    public void save() {
        _save(configXml);
    }
}
