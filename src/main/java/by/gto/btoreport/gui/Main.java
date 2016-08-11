package by.gto.btoreport.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import by.avest.crypt.util.AvJavaSecKitConfig;

import java.io.File;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.stream.Stream;

public class Main extends Application {

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
    public void start(Stage primaryStage) throws Exception {
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


    public static void main(String[] args) {
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

    private static void initAvest() {

        System.out.println("user.home: " + System.getProperty("user.home"));
        System.out.println("APPDATA: " + System.getenv("APPDATA"));
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
            System.out.println("dllPath: " + dllPath);
            System.setProperty("java.library.path", dllPath);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Authenticator.setDefault(new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(System.getProperty("https.proxyUser"), System.getProperty("https.proxyPass").toCharArray());
            }
        });
    }
}
