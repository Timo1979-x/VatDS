package by.gto.tools;

import by.gto.jasperprintmysql.App;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

/**
 *
 * @author Aleks
 */
public class ConfigReader {

    private static volatile ConfigReader instance;
    private static String host = "localhost";
    private static String port = "33060";
    private static String chiefDS = "ФИО";
    private static String position = "Должность";
    private static int NDS = 20;
    //private File configXml = new File("config.xml");
    private static File configXml = new File("config.xml");

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
            save(configXml);
        }
        try {
            config = new XMLConfiguration("config.xml");
        } catch (ConfigurationException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        read();
    }

    private void save(File configXml) {
        try {
            Writer wFile = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configXml), "UTF8"));
            wFile.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>\n");
            wFile.write("<configuration>\n");
            wFile.write("    <config>\n");
            wFile.write(String.format("        <host>%s</host>\n", getHost()));
            wFile.write(String.format("        <port>%s</port>\n", getPort()));
            wFile.write(String.format("        <position>%s</position>\n", getPosition()));
            wFile.write(String.format("        <chiefDS>%s</chiefDS>\n", getChiefDS()));
            wFile.write(String.format("        <NDS>%s</NDS>\n", getNDS()));
            wFile.write("    </config>\n");
            wFile.write("</configuration>\n");
            wFile.flush();
        } catch (IOException ex) {
            Logger.getLogger(ConfigReader.class.getName()).log(Level.SEVERE, null, ex);
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
            host = config.getString("config.host").trim();
            port = config.getString("config.port").trim();
            position = config.getString("config.position").trim();
            chiefDS = config.getString("config.chiefDS").trim();
            NDS = Integer.parseInt(config.getString("config.NDS").trim());
        } catch (ConfigurationException ex) {
            Logger.getLogger(ConfigReader.class.getName()).log(Level.SEVERE, null, ex);
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
        ConfigReader.host = host;
    }

    public void setChiefDS(String chiefDS) {
        ConfigReader.chiefDS = chiefDS;
    }

    public void setPosition(String position) {
        ConfigReader.position = position;
    }

    public void setNDS(int NDS) {
        ConfigReader.NDS = NDS;
    }

    public void setSave() {
        save(configXml);
    }
}