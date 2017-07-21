package by.gto.helpers;

import by.gto.model.BranchInfo;
import by.gto.model.VatData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableRow;

public class BranchComboBoxCell extends TableCell<VatData, Integer> {

    private ComboBox<BranchInfo> comboBox;

    @Override
    public void startEdit() {
        TableRow<VatData> currentRow = getTableRow();
        ObservableList<BranchInfo> branchOptions = currentRow.getItem().getBranches();
        if(branchOptions == null) {
            return;
        }
        super.startEdit();

        if (comboBox == null) {
            createComboBox(branchOptions);
        }

        setGraphic(comboBox);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
//        comboBox.show();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        Integer item = getItem();
        setText(item==null?null:item.toString());
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    public void updateItem(Integer item, boolean empty) {
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
                if(vd.getBranch()==null && vd.getBranches() != null) {
                    this.setStyle("-fx-background-color:#FFB3BA;");
                } else {
                    this.setStyle("");
                }
            }
        }
    }

    private void createComboBox(ObservableList<BranchInfo> agreementOptions) {
        comboBox = new ComboBox<>(FXCollections.observableArrayList(agreementOptions));
        comboBox.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
        comboBox.setOnAction(event -> {
            BranchInfo selectedItem = comboBox.getSelectionModel().getSelectedItem();
            commitEdit(selectedItem.getBranchCode());
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
