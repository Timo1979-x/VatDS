package by.gto.btoreport.gui;

import by.gto.tools.ConfigReader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.cxf.common.i18n.Exception;
import org.apache.log4j.*;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URISyntaxException;

public class Main extends Application {
    private static String dataDir = "d:\\";
    private static final Logger log = Logger.getLogger(Main.class);
    private static Stage stage = null;
    // тестовая площадка ЭСЧФ:
    //public static final String EINV_PORTAL_URL = "https://185.32.226.170:4443/InvoicesWS/services/InvoicesPort?wsdl";
    //public static final String EINV_PORTAL_URL= "https://ws.vat.gov.by:443/InvoicesWS/services/InvoicesPort?wsdl";
    public static boolean verbose = true;
    public static boolean debug = true;

    public static String message;

    public static Stage getStage() {
        return stage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception, IOException {
        //primaryStage.setOnCloseRequest(e -> System.exit(0));
        stage = primaryStage;
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(Main.class.getClassLoader().getResource("fxml/main.fxml"));
        primaryStage.setTitle("Отчеты для ДС");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(true);
        Image i = new Image(Main.class.getClassLoader().getResourceAsStream("piggy-bank-icon.png"));
        primaryStage.getIcons().add(i);

        primaryStage.show();
    }


    public static String getDataDir() {
        return dataDir;
    }

    public static void main(String[] args) throws IOException {
        dataDir = System.getenv("APPDATA") + "\\Beltehosmotr\\btoReportNG";
        ConfigReader.setFilePath(dataDir + "\\config.xml");
        try {
            initLogger();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        }
//        try {
//            try {
//                FileInputStream fis = new FileInputStream("nonexistent file");
//                fis.read(new byte[20]);
//            } catch (IOException ex) {
//                //log.error(ex.getMessage(), ex);
//                final SAXException la = new SAXException("la", ex);
//                la.initCause(ex);
//                throw la;
//            }
//        } catch (SAXException ex) {
//            log.error(ex.getMessage(), ex);
//        }
            //поддерживаемые настройки (можно задавать через ком. строку)
            // все отладочные сообщения java security (см. http://docs.oracle.com/javase/7/docs/technotes/guides/security/troubleshooting-security.html):
            // -Djava.security.debug=none
            // пароль по умолчанию
            // -Dby.gto.btoreport.avest.password="..."
            // название ключа по умолчанию:
            // -Dby.gto.btoreport.avest.alias="Республиканское унитарное сервисное предприятие \"БЕЛТЕХОСМОТР\"_02_06_16_17_17"
            // можно задать тестовую площадку:
            // -Dby.gto.btoreport.avest.url="https://185.32.226.170:4443/InvoicesWS/services/InvoicesPort?wsdl"
            initAvest();

            launch(args);
        }

    private static void initLogger() throws IOException {
        File logDir = new File(dataDir + "\\log");
        logDir.mkdirs();
        final RollingFileAppender rfAppender = new RollingFileAppender(
                new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"),
                logDir.getAbsolutePath() + "\\btoReportNG.log", true);
        rfAppender.setThreshold(Level.WARN);
        Logger.getRootLogger().addAppender(rfAppender);
//        ConsoleAppender cAppender = new ConsoleAppender(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"), "System.err");
//        cAppender.setThreshold(Level.ALL);
//        Logger.getRootLogger().addAppender(cAppender);
    }

    private static void initAvest() {
        boolean is64bit;
        if (System.getProperty("os.name").contains("Windows")) {
            is64bit = (System.getenv("ProgramFiles(x86)") != null);
        } else {
            is64bit = (System.getProperty("os.arch").indexOf("64") != -1);
        }
        System.setProperty("by.avest.loader.shared", "true");
        //System.setProperty("java.util.logging.config.file", "OFF"); //?????


        try {
            String f = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();
            String dllPath = f + "\\win" + (is64bit ? "64" : "32");
            System.setProperty("java.library.path", dllPath);
            log.info("dllPath: " + dllPath);
            log.info("java.library.path" + dllPath);
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }
}
