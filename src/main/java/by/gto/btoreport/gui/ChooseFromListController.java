package by.gto.btoreport.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public final class ChooseFromListController implements Initializable {

    @FXML
    public ListView lList;
    @FXML
    public TextField ePassword;
    @FXML
    public Label lMessage;
    private int keyIndex;
    private String password;
    @FXML
    public Button bCancel;


    private ObservableList<String> listItems = FXCollections.observableArrayList();

    public void bCancelAction(ActionEvent actionEvent) {
        keyIndex = -1;
        password = null;
        Stage stage = (Stage) bCancel.getScene().getWindow();
        stage.close();
    }

    public void bOkAction(ActionEvent actionEvent) {
        keyIndex = lList.getSelectionModel().getSelectedIndex();
        password = ePassword.getText();
        Stage stage = (Stage) bCancel.getScene().getWindow();
        stage.close();
    }


    public void message(String msg) {
        lMessage.setText(msg);
    }

    public int getKeyIndex() {
        return keyIndex;
    }

    public String getPassword() {
        return password;
    }

    public void setListItems(String[] items) {
        listItems.clear();
        listItems.addAll(items);
        lList.getSelectionModel().select((items.length == 1) ? 0 : -1);
        for(int i = 0; i<items.length; i++) {
            if("Республиканское унитарное сервисное предприятие \"БЕЛТЕХОСМОТР\"_02_06_16_17_17".equals(items[i])) {
                lList.getSelectionModel().select(i);
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lList.setItems(listItems);
    }
}
