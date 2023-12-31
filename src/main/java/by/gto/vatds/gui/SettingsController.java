package by.gto.vatds.gui;

import by.gto.controllers.BaseController;
import by.gto.tools.ConfigReader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import org.apache.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SettingsController extends BaseController<Void, Void> implements javafx.fxml.Initializable {

    @FXML
    public TextField eServerIP;
    @FXML
    public TextField eVAT;
    @FXML
    public TextField eUNP;
    @FXML
    public TextField eOrgName;
    @FXML
    public Label lResult;
    @FXML
    public TextField eServiceName;
    @FXML
    public TextField eVatPath;

    @FXML
    public TextField eProxyHost;
    @FXML
    public TextField eProxyPort;
    @FXML
    public TextField eProxyUser;
    @FXML
    public PasswordField eProxyPass;
    @FXML
    public TextField eOrgAddress;
    @FXML
    public CheckBox cbUseProxy;
    private final static Logger log = Logger.getLogger(SettingsController.class);

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final ConfigReader instance = ConfigReader.getInstance();

        eServerIP.setText(instance.getHost());

        eUNP.setText(String.valueOf(instance.getUNP()));
        eOrgName.setText(instance.getOrgName());
        eOrgAddress.setText(instance.getOrgAddress());
        eServiceName.setText(instance.getServiceName());
        eVatPath.setText(instance.getVatPath());

        eProxyHost.setText(instance.getProxyHost());
        eProxyPort.setText(String.valueOf(instance.getProxyPort()));
        eProxyUser.setText(instance.getProxyUser());
        eProxyPass.setText(instance.getProxyPass());
        cbUseProxy.setSelected(instance.isUseProxy());
        useProxyChanged();
    }

    public void bSaveClick(ActionEvent actionEvent) {
        lResult.setText(null);
        final ConfigReader instance = ConfigReader.getInstance();
        String ip = eServerIP.getText().trim();
        if (!validateIP(ip)) {
            lResult.setText("Недопустимый IP адрес сервера БД");
            return;
        }

        String unp = eUNP.getText().trim();
        if (!verifyUNP(unp)) {
            lResult.setText("УНП обязан состоять ровно из 9 цифр");
            return;
        }

        File dir = new File(eVatPath.getText());
        dir.mkdirs();
        if (!(dir.exists() && dir.isDirectory())) {
            lResult.setText("\"" + dir + "\" не существует или не папка");
            return;
        }

        if(cbUseProxy.isSelected()) {
            int proxyPort = -1;
            try {
                proxyPort = Integer.parseInt(eProxyPort.getText());
            } catch (NumberFormatException e) {

            }
            if (proxyPort <= 0) {
                lResult.setText("Порт должен быть положительным числом");
                return;
            }
            instance.setProxyPort(proxyPort);
        }

        instance.setHost(ip);
        instance.setUNP(Integer.valueOf(unp));
        instance.setOrgName(eOrgName.getText());
        instance.setOrgAddress(eOrgAddress.getText());
        instance.setServiceName(eServiceName.getText());
        instance.setVatPath(eVatPath.getText());
        instance.setUseProxy(cbUseProxy.isSelected());
        instance.setProxyHost(eProxyHost.getText());
        instance.setProxyUser(eProxyUser.getText());
        instance.setProxyPass(eProxyPass.getText());

        instance.save();
        stage.close();
    }

    private boolean verifyVAT(String vat) {
        try {
            int _vat = Integer.parseInt(vat);
            return _vat >= 0 && _vat <= 30;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean verifyUNP(String unp) {
        try {
            final long l = Long.parseLong(unp);
            return l >= 100000000 && l <= 999999999;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean validateIP(String IPvalue) {
        boolean res = false;
        String errorString = "";
        String theName = "IP адрес ";
        Pattern ipPattern = Pattern.compile("(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})");
        Matcher ipMatcher = ipPattern.matcher(IPvalue);
        if (ipMatcher.matches()) {
            StringTokenizer st = new StringTokenizer(IPvalue, ".");
            int[] ipArray = new int[4];
            int i = 0;
            while (st.hasMoreElements()) {
                String s = st.nextElement().toString();
                ipArray[i] = Integer.parseInt(s);
                i++;
                log.info("s:" + s);
            }
            for (int j = 0; j < 4; j++) {
                log.info("ipArray[i]:" + ipArray[j]);
            }
            switch (IPvalue) {
                case "0.0.0.0":
                    errorString += theName + ": " + IPvalue + " это специальный IP адрес и не может быть использован.";
                    break;
                case "255.255.255.255":
                    errorString += theName + ": " + IPvalue + " это специальный IP адрес и не может быть использован.";
                    break;
            }
            if (ipArray == null) {
                errorString += theName + ": " + IPvalue + " не допустимый IP адрес.";
            } else {

                for (int k = 0; k < 4; k++) {
                    int thisSegment = ipArray[k];
                    if (thisSegment > 255) {
                        errorString += theName + ": " + IPvalue + " не допустимый IP адрес.";
                        k = 4;
                    }
                    if ((k == 0) && (thisSegment > 255)) {
                        errorString += theName + ": " + IPvalue + " это специальный IP адрес и не может быть использован.";
                        k = 4;
                    }
                }
            }
        } else {
            errorString += theName + ": " + IPvalue + " не допустимый IP адрес.";
        }

        if (errorString.isEmpty()) {
            res = true;
            //   JOptionPane.showMessageDialog(null, "Введенный IP адрес - корректный!", "My custom dialog", JOptionPane.INFORMATION_MESSAGE);
        } else {
            Alert a = new Alert(Alert.AlertType.ERROR, errorString, ButtonType.CLOSE);
            a.showAndWait();
            //JOptionPane.showMessageDialog(null, errorString, "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
        return res;
    }

    public void bBrowseVatPathAction(ActionEvent actionEvent) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Укажите папку для сохранения файлов ЭСЧФ");
        final File initialDir = new File(eVatPath.getText());
        if (initialDir.isDirectory() && initialDir.exists()) {
            dc.setInitialDirectory(initialDir);
        }
        try {
            File out = dc.showDialog(Main.getStage());
            if (null != out) {
                eVatPath.setText(out.getAbsolutePath());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public void cbUseProxyAction(ActionEvent actionEvent) {
        useProxyChanged();
    }

    private void useProxyChanged() {
        final boolean selected = cbUseProxy.isSelected();
        eProxyHost.setDisable(!selected);
        eProxyPort.setDisable(!selected);
        eProxyPass.setDisable(!selected);
        eProxyUser.setDisable(!selected);
    }

}
