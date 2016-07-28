package by.gto.btoreport.gui;

import by.gto.tools.ConfigReader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SettingsController implements javafx.fxml.Initializable {

    @FXML
    public TextField eServerIP;
    @FXML
    public TextField ePosition;
    @FXML
    public TextField eFIO;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final ConfigReader instance = ConfigReader.getInstance();

        eServerIP.setText(instance.getHost());
        ePosition.setText(instance.getPosition());
        eFIO.setText(instance.getChiefDS());
        eVAT.setText(String.valueOf(instance.getNDS()));

        eUNP.setText(String.valueOf(instance.getUNP()));
        eOrgName.setText(instance.getOrgName());
        eServiceName.setText(instance.getServiceName());
        eVatPath.setText(instance.getVatPath());
    }

    public void bSaveClick(ActionEvent actionEvent) {
        String ip = eServerIP.getText().trim();
        if (!validateIP(ip)) {
            lResult.setText("Недопустимый IP адрес");
            return;
        }

        String unp = eUNP.getText().trim();
        if (!verifyUNP(unp)) {
            lResult.setText("УНП обязан состоять ровно из 9 цифр");
            return;
        }

        String vat = eVAT.getText();
        if (!verifyVAT(vat)) {
            lResult.setText("Неверная ставка НДС");
            return;
        }

        File dir = new File(eVatPath.getText());
        if(!(dir.exists() && dir.isDirectory())) {
            lResult.setText("\"" + dir + "\" не существует или не папка");
            return;
        }

        final ConfigReader instance = ConfigReader.getInstance();
        instance.setHost(ip);
        instance.setPosition(ePosition.getText().trim());
        instance.setChiefDS(eFIO.getText().trim());
        instance.setNDS(Integer.valueOf(vat));
        instance.setUNP(Integer.valueOf(unp));
        instance.setOrgName(eOrgName.getText());
        instance.setServiceName(eServiceName.getText());
        instance.setVatPath(eVatPath.getText());

        instance.save();
        lResult.setText("Успешно сохранено");
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
            Long.parseLong(unp);
            return unp.length() == 9;
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
                System.out.println("s:" + s);
            }
            for (int j = 0; j < 4; j++) {
                System.out.println("ipArray[i]:" + ipArray[j]);
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
            System.out.println(e.getMessage());
        }
    }
}
