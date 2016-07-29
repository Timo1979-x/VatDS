package by.gto.btoreport.gui;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.IndexRange;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * Created by ltv on 29.07.2016.
 */
public class AutoCompleteComboBoxListener<T> implements EventHandler<KeyEvent> {

    private ComboBox comboBox;
    private StringBuilder sb;
    private int lastLength;

    public AutoCompleteComboBoxListener(ComboBox<T> comboBox) {
        this.comboBox = comboBox;
        sb = new StringBuilder();

        this.comboBox.setEditable(true);
        this.comboBox.setOnKeyReleased(AutoCompleteComboBoxListener.this);

        // add a focus listener such that if not in focus, reset the filtered typed keys
        this.comboBox.getEditor().focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    // in focus
                } else {
                    lastLength = 0;
                    sb.delete(0, sb.length());
                    selectClosestResultBasedOnTextFieldValue(false, false);
                }
            }
        });

        this.comboBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                selectClosestResultBasedOnTextFieldValue(true, true);
            }
        });
    }

    @Override
    public void handle(KeyEvent event) {
        // this variable is used to bypass the auto complete process if the length is the same.
        // this occurs if user types fast, the length of textfield will record after the user
        // has typed after a certain delay.
        if (lastLength != (comboBox.getEditor().getLength() - comboBox.getEditor().getSelectedText().length()))
            lastLength = comboBox.getEditor().getLength() - comboBox.getEditor().getSelectedText().length();

        if (event.isControlDown() || event.getCode() == KeyCode.BACK_SPACE ||
                event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT ||
                event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.HOME ||
                event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB
                )
            return;

        IndexRange ir = comboBox.getEditor().getSelection();
        sb.delete(0, sb.length());
        sb.append(comboBox.getEditor().getText());
        // remove selected string index until end so only unselected text will be recorded
        try {
            sb.delete(ir.getStart(), sb.length());
        } catch (Exception e) {
        }

        ObservableList<T> items = comboBox.getItems();
        for (int i = 0; i < items.size(); i++) {
            T item = items.get(i);
            String itemString;
            if (item == null) {
                itemString = "";
            } else {
                itemString = item.toString();
            }

            if (itemString.toLowerCase().startsWith(comboBox.getEditor().getText().toLowerCase())) {
                try {
                    comboBox.getEditor().setText(sb.toString() + itemString.substring(sb.toString().length()));
                } catch (Exception e) {
                    comboBox.getEditor().setText(sb.toString());
                }
                comboBox.getEditor().positionCaret(sb.toString().length());
                comboBox.getEditor().selectEnd();
                break;
            }
        }
    }

    /*
     * selectClosestResultBasedOnTextFieldValue() - selects the item and scrolls to it when
     * the popup is shown.
     *
     * parameters:
     *  affect - true if combobox is clicked to show popup so text and caret position will be readjusted.
     *  inFocus - true if combobox has focus. If not, programmatically press enter key to add new entry to list.
     *
     */
    private void selectClosestResultBasedOnTextFieldValue(boolean affect, boolean inFocus) {
        ObservableList<T> items = AutoCompleteComboBoxListener.this.comboBox.getItems();
        boolean found = false;

        for (int i = 0; i < items.size(); i++) {
            T item= items.get(i);
            String itemString;
            if(item==null) {
                itemString="";
            } else {
                itemString = items.toString();
            }

            if (AutoCompleteComboBoxListener.this.comboBox.getEditor().getText().toLowerCase().equals(itemString.toLowerCase())) {
                try {
                    ListView lv = ((ComboBoxListViewSkin) AutoCompleteComboBoxListener.this.comboBox.getSkin()).getListView();
                    lv.getSelectionModel().clearAndSelect(i);
                    lv.scrollTo(lv.getSelectionModel().getSelectedIndex());
                    found = true;
                    break;
                } catch (Exception e) {
                }
            }
        }

        String s = comboBox.getEditor().getText();
        if (!found && affect) {
            comboBox.getSelectionModel().clearSelection();
            comboBox.getEditor().setText(s);
            comboBox.getEditor().end();
        }

        if (!inFocus && comboBox.getEditor().getText() != null && comboBox.getEditor().getText().trim().length() > 0) {
            // press enter key programmatically to have this entry added
            //KeyEvent ke = KeyEvent.impl_keyEvent(comboBox, KeyCode.ENTER.toString(), KeyCode.ENTER.getName(), KeyCode.ENTER.impl_getCode(), false, false, false, false, KeyEvent.KEY_RELEASED);
            KeyEvent ke = new KeyEvent(KeyEvent.KEY_RELEASED, KeyCode.ENTER.toString(), KeyCode.ENTER.getName(), KeyCode.ENTER, false, false, false, false);
            comboBox.fireEvent(ke);
        }
    }

}