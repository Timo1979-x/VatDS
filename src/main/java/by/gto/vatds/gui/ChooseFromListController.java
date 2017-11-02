package by.gto.vatds.gui;

import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public final class ChooseFromListController implements Initializable {

    @FXML
    public ListView lList;
    @FXML
    public TextField ePassword;
    @FXML
    public Label lMessage;
    @FXML
    public Button bOk;
    private int keyIndex;
    private String password;
    private String alias;
    @FXML
    public Button bCancel;


    private ObservableList<String> listItems = FXCollections.observableArrayList();

    public void bCancelAction(ActionEvent actionEvent) {
        keyIndex = -1;
        password = null;
        alias = null;
        Stage stage = (Stage) bCancel.getScene().getWindow();
        stage.close();
    }

    public void bOkAction(ActionEvent actionEvent) {
        closeOk();
    }

    public void closeOk() {
        keyIndex = lList.getSelectionModel().getSelectedIndex();
        alias = (String) lList.getSelectionModel().getSelectedItem();
        password = ePassword.getText();
        Stage stage = (Stage) bCancel.getScene().getWindow();
        stage.close();
    }


    public void message(String msg) {
        lMessage.setText(msg);
    }

    private int getKeyIndex() {
        return keyIndex;
    }

    public String getAlias() {
        return alias;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String pass) {
        ePassword.setText(pass);
    }

    public void setListItems(List<String> items) {
        listItems.clear();
        listItems.addAll(items);
        lList.getSelectionModel().clearSelection();
//        lList.getSelectionModel().select((items.size() == 1) ? 0 : -1);

//        final String alias = System.getProperty("by.gto.vatds.avest.alias");
//        if (alias != null) {
//            for (int i = 0; i < items.size(); i++) {
//                if (alias.equals(items[i])) {
//                    lList.getSelectionModel().select(i);
//                }
//            }
//        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lList.setItems(listItems);

        bOk.disableProperty().bind(
                lList.getSelectionModel().selectedIndexProperty().isEqualTo(-1).or(ePassword.lengthProperty().isEqualTo(0))
        );
        Platform.runLater(() -> ePassword.requestFocus());
    }

    public void lListOnMouseClicked(MouseEvent mouseEvent) {
        if (MouseEvent.MOUSE_CLICKED == mouseEvent.getEventType() &&
                mouseEvent.getButton() == MouseButton.PRIMARY &&
                mouseEvent.getClickCount() == 2) {
            closeOk();
        }
    }
}
