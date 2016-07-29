package by.gto.btoreport.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import org.apache.commons.lang.StringUtils;

import java.net.URL;
import java.util.ResourceBundle;

public final class PasswordPromptController implements Initializable {

    public Label lMessage;
    public Label lCaption;
    private String result;
    @FXML
    public Button bCancel;
    @FXML
    public Label lPrompt;
    @FXML
    public PasswordField ePassword;

    public void bCancelAction(ActionEvent actionEvent) {
        result = null;
        Stage stage = (Stage) bCancel.getScene().getWindow();
        stage.close();
    }

    public String getResult() {
        return result;
    }

    public void bOkAction(ActionEvent actionEvent) {
        result = ePassword.getText();
        Stage stage = (Stage) bCancel.getScene().getWindow();
        stage.close();
    }


    public void setMessage(String msg)
    {
        if(StringUtils.isEmpty(msg)) {
            lMessage.setVisible(false);
        } else {
            lMessage.setText(msg);
            lMessage.setVisible(true);
        }
    }

    public void setCaption(String title) {
        lCaption.setText(title);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> ePassword.requestFocus());
    }
}
