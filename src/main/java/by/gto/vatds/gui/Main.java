package by.gto.vatds.gui;

import by.gto.helpers.PathHelpers;
import by.gto.tools.ConfigReader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.cxf.common.i18n.Exception;
import org.apache.log4j.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main extends Application {
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
        stage = primaryStage;
        FXMLLoader loader = new FXMLLoader(Main.class.getClassLoader().getResource("fxml/main.fxml"));
        Parent root = loader.load();
        MainController controller = loader.<MainController>getController();
        primaryStage.setTitle("Выставление ЭСЧФ");
        final Scene rootScene = new Scene(root);
        primaryStage.setScene(rootScene);
        controller.setScene(rootScene);
        primaryStage.setResizable(true);
        Image i = new Image(Main.class.getClassLoader().getResourceAsStream("piggy-bank-icon.png"));
        primaryStage.getIcons().add(i);

        primaryStage.show();
    }

    public static void main(String[] args) throws IOException {
        ConfigReader.setFilePath(PathHelpers.getDataDirectory() + "\\config.xml");
//        try {
//            initLogger();
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//            throw e;
//        }

        //поддерживаемые настройки (можно задавать через ком. строку)
        // все отладочные сообщения java security (см. http://docs.oracle.com/javase/7/docs/technotes/guides/security/troubleshooting-security.html):
        // -Djava.security.debug=none
        // пароль по умолчанию
        // -Dby.gto.vatds.avest.password="..."
        // название ключа по умолчанию:
        // -Dby.gto.vatds.avest.alias="Республиканское унитарное сервисное предприятие \"БЕЛТЕХОСМОТР\"_02_06_16_17_17"
        // можно задать тестовую площадку:
        // -Dby.gto.vatds.avest.url="https://185.32.226.170:4443/InvoicesWS/services/InvoicesPort?wsdl"
//            initAvest();

        launch(args);
    }

    public static void initLogger() throws IOException {
        File logDir = new File(PathHelpers.getDataDirectory() + "\\log");
        logDir.mkdirs();
        final RollingFileAppender rfAppender = new RollingFileAppender(
                new PatternLayout("%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n"),
                logDir.getAbsolutePath() + "\\vatDS.log", true);
        rfAppender.setThreshold(Level.WARN);
        rfAppender.setMaximumFileSize(1024 * 1024);
        Logger.getRootLogger().addAppender(rfAppender);
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
