package by.gto.btoreport.gui;

import com.sun.javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.IndexRange;
import javafx.scene.control.ListView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.apache.log4j.Logger;

/**
 * Created by ltv on 29.07.2016.
 * based on http://tech.chitgoks.com/2013/08/20/how-to-create-autocomplete-combobox-or-textfield-in-java-fx-2/
 */
public class AutoCompleteComboBoxListenerBB<T> implements EventHandler<KeyEvent> {

    private ComboBox comboBox;
    private StringBuilder sb;
    private int lastLength;
    private ObservableList<T> originalItems;

    private boolean itemsUpdateInProgress;
    private final static Logger log = Logger.getLogger(AutoCompleteComboBoxListenerBB.class);
    public AutoCompleteComboBoxListenerBB(ComboBox<T> comboBox) {
        this.comboBox = comboBox;
        sb = new StringBuilder();
        this.originalItems = FXCollections.observableArrayList(comboBox.getItems());
        this.comboBox.setEditable(true);
        this.comboBox.setOnKeyReleased(AutoCompleteComboBoxListenerBB.this);
        this.comboBox.getItems().addListener(new ListChangeListener<T>() {
            @Override
            public void onChanged(Change<? extends T> c) {
                if (itemsUpdateInProgress) return;
                AutoCompleteComboBoxListenerBB.this.originalItems =
                        FXCollections.observableArrayList(comboBox.getItems());
            }
        });
        this.comboBox.itemsProperty().addListener(new ChangeListener<ObservableList<T>>() {
            @Override
            public void changed(ObservableValue<? extends ObservableList<T>> observable, ObservableList<T> oldValue, ObservableList<T> newValue) {
                if (itemsUpdateInProgress) return;
                AutoCompleteComboBoxListenerBB.this.originalItems = FXCollections.observableArrayList(newValue);
            }
        });

        // add a focus listener such that if not in focus, reset the filtered typed keys
        this.comboBox.getEditor().focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    // in focus
                    AutoCompleteComboBoxListenerBB.this.comboBox.show();
                } else {
                    lastLength = 0;
                    sb.delete(0, sb.length());
                    selectClosestResultBasedOnTextFieldValue(false, false);
                    AutoCompleteComboBoxListenerBB.this.comboBox.hide();
                }
            }
        });

        this.comboBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                selectClosestResultBasedOnTextFieldValue(true, true);
            }
        });

//        this.comboBox.focusedProperty().addListener(new ChangeListener<Boolean>() {
//            @Override
//            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
//                if (newPropertyValue) {
//                    System.out.println("Textfield on focus");
//                    AutoCompleteComboBoxListenerBB.this.comboBox.show();
//                } else {
//                    System.out.println("Textfield out focus");
//                    //AutoCompleteComboBoxListenerBB.this.comboBox.hide();
//                }
//            }
//        });
    }

    @Override
    public void handle(KeyEvent event) {
//        if (event.getCode() == KeyCode.BACK_SPACE) {
//            System.out.println(event.getCode());
//        }
        // this variable is used to bypass the auto complete process if the length is the same.
        // this occurs if user types fast, the length of textfield will record after the user
        // has typed after a certain delay.
        if (lastLength != (comboBox.getEditor().getLength() - comboBox.getEditor().getSelectedText().length()))
            lastLength = comboBox.getEditor().getLength() - comboBox.getEditor().getSelectedText().length();

        if (event.isControlDown() || event.getCode() == KeyCode.BACK_SPACE ||
        //if (event.isControlDown() ||
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
            log.error(e.getMessage());
        }

        //ObservableList items = comboBox.getItems();
        //ObservableList items = originalItems;

        final ObservableList<T> newItems = FXCollections.observableArrayList();
        for (int i = 0; i < originalItems.size(); i++) {
            T item = originalItems.get(i);
            String itemString;
            if (item == null) {
                itemString = "";
            } else {
                itemString=item.toString();
            }

            if (itemString.toLowerCase().startsWith(comboBox.getEditor().getText().toLowerCase())) {
//                originalItems.stream().filter(
//                        e -> sb.length() == 0 ||
//                                (e != null && e.toString().toLowerCase().startsWith(sb.toString()))
//                ).forEach(e -> newItems.add(e));
                newItems.add(item);
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
        itemsUpdateInProgress = true;
        comboBox.setItems(newItems);
        itemsUpdateInProgress = false;
        comboBox.show();
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
        //ObservableList items = AutoCompleteComboBoxListenerBB.this.comboBox.getItems();
        //ObservableList items = originalItems;

        boolean found = false;
        itemsUpdateInProgress=true;
        for (int i = 0; i < originalItems.size(); i++) {
            Object item1 = originalItems.get(i);
            String itemString;
            if (item1 == null) {
                itemString = "";
            } else {
                itemString = item1.toString().toLowerCase();
            }
            if (AutoCompleteComboBoxListenerBB.this.comboBox.getEditor().getText().toLowerCase().equals(itemString)) {
                try {
                    ListView lv = ((ComboBoxListViewSkin) AutoCompleteComboBoxListenerBB.this.comboBox.getSkin()).getListView();
                    lv.getSelectionModel().clearAndSelect(i);
                    lv.scrollTo(lv.getSelectionModel().getSelectedIndex());
                    found = true;
                    break;
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
        itemsUpdateInProgress=false;

        String s = comboBox.getEditor().getText();
        if (!found && affect) {
            comboBox.getSelectionModel().clearSelection();
            comboBox.getEditor().setText(s);
            comboBox.getEditor().end();
        }

        if (!inFocus && comboBox.getEditor().getText() != null && comboBox.getEditor().getText().trim().length() > 0) {
            // press enter key programmatically to have this entry added
            KeyEvent ke = new KeyEvent(KeyEvent.KEY_RELEASED, KeyCode.ENTER.toString(), KeyCode.ENTER.getName(), KeyCode.ENTER, false, false, false, false);
            //KeyEvent ke = KeyEvent.impl_keyEvent(comboBox, KeyCode.ENTER.toString(), KeyCode.ENTER.getName(), KeyCode.ENTER.impl_getCode(), false, false, false, false, KeyEvent.KEY_RELEASED);
            comboBox.fireEvent(ke);
        }
    }

}