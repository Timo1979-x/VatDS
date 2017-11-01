package by.gto.helpers;

import by.gto.model.AgreementData;
import by.gto.model.VatData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class AgreementComboBoxCell extends TableCell<VatData, String> {

    private ComboBox<AgreementData> comboBox;

    @Override
    public void startEdit() {
//        System.out.println("startEdit");
        TableRow<VatData> currentRow = getTableRow();
        ObservableList<AgreementData> agreementOptions = currentRow.getItem().getAgreementOptions();
        if (agreementOptions == null) {
            return;
        }
        super.startEdit();

        if (comboBox == null) {
            createComboBox(agreementOptions);
        }

        setGraphic(comboBox);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
//        comboBox.show();
    }

    @Override
    public void cancelEdit() {
//        System.out.println("cancelEdit");
        super.cancelEdit();

        setText(getItem());
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    public void updateItem(String item, boolean empty) {
//        System.out.println("updateItem");
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
//                if (comboBox != null) {
//                    comboBox.setText(getString());
//                }
                setGraphic(comboBox);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } else {
                setText(getString());
                setContentDisplay(ContentDisplay.TEXT_ONLY);
                TableRow<VatData> currentRow = getTableRow();
                VatData vd = currentRow.getItem();
                if(vd != null) {
                    if (vd.getAgreementOptions() != null) {
                        this.setStyle("-fx-background-color:#CAFFA8;");
                    } else if (vd.getAgreementNumber() == null || vd.getAgreementDate() == null) {
                        this.setStyle("-fx-background-color:#FFB3BA;");
                    } else {
                        this.setStyle("");
                    }
                }
            }
        }
    }

    private void createComboBox(ObservableList<AgreementData> agreementOptions) {
        comboBox = new ComboBox<>(FXCollections.observableArrayList(agreementOptions));
        comboBox.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        comboBox.setOnAction(event -> {
            AgreementData selectedItem = comboBox.getSelectionModel().getSelectedItem();
            System.out.println(selectedItem);
            commitEdit(selectedItem.getNumber());
            TableRow<VatData> currentRow = getTableRow();
            currentRow.getItem().setAgreementDate(selectedItem.getDate());
        });
        comboBox.setOnHidden(event -> {
                    System.out.println("onHidden, selectedIndex " + comboBox.getSelectionModel().getSelectedIndex() +
                            " selectedItem = " + comboBox.getSelectionModel().getSelectedItem());

                }
        );
//        comboBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
//
//            @Override
//            public void handle(KeyEvent t) {
//                if (t.getCode() == KeyCode.ENTER) {
//                    commitEdit(comboBox.getSelectionModel().getSelectedItem());
//                    System.out.println("KeyCode.ENTER");
//                } else if (t.getCode() == KeyCode.ESCAPE) {
//                    System.out.println("KeyCode.ESCAPE");
//                    cancelEdit();
//                }
//            }
//        });
    }

    private String getString() {
        return getItem() == null ? "" : getItem().toString();
    }
}
