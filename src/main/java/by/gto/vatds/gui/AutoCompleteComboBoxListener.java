package by.gto.vatds.gui;

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
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.apache.log4j.Logger;

/**
 * создано по мотивам http://tech.chitgoks.com/2013/08/20/how-to-create-autocomplete-combobox-or-textfield-in-java-fx-2/
 * @param <T>
 */
public class AutoCompleteComboBoxListener<T> implements EventHandler<KeyEvent> {

    private final static Logger log = Logger.getLogger(AutoCompleteComboBoxListener.class);
    private ComboBox<T> comboBox;
    private StringBuilder sb;
    private ObservableList<T> originalItems;

    public AutoCompleteComboBoxListener(ComboBox<T> comboBox) {
        this.comboBox = comboBox;
        sb = new StringBuilder();
        this.originalItems = FXCollections.observableArrayList(comboBox.getItems());

        this.comboBox.getItems().addListener(new ListChangeListener<T>() {
            @Override
            public void onChanged(Change<? extends T> c) {
                AutoCompleteComboBoxListener.this.originalItems =
                        FXCollections.observableArrayList(comboBox.getItems());
                if (AutoCompleteComboBoxListener.this.originalItems.size() == 0) {
                    log.info("originalItems.size() " + originalItems.size());
                }
            }
        });
        this.comboBox.itemsProperty().addListener(new ChangeListener<ObservableList<T>>() {
            @Override
            public void changed(ObservableValue<? extends ObservableList<T>> observable, ObservableList<T> oldValue, ObservableList<T> newValue) {
                AutoCompleteComboBoxListener.this.originalItems =
                        FXCollections.observableArrayList(newValue);
                if (AutoCompleteComboBoxListener.this.originalItems.size() == 0) {
                    log.info("originalItems.size() " + originalItems.size());
                }
            }
        });


        this.comboBox.setEditable(true);
        this.comboBox.setOnKeyReleased(AutoCompleteComboBoxListener.this);

        // add a focus listener such that if not in focus, reset the filtered typed keys
        this.comboBox.getEditor().focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue observable, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    AutoCompleteComboBoxListener.this.comboBox.show();
                    showFilteredList();
                } else {
                    AutoCompleteComboBoxListener.this.comboBox.hide();
                }
            }
        });

//        this.comboBox.setOnMouseClicked(new EventHandler<MouseEvent>() {
//            @Override
//            public void handle(MouseEvent event) {
//                System.out.printf("this.comboBox.setOnMouseClicked EventHandler<MouseEvent>()");
//                selectClosestResultBasedOnTextFieldValue(true, true);
//            }
//        });
    }

    private void showFilteredList() {
        boolean found = false;
        ObservableList<T> items = comboBox.getItems();
//        ObservableList<T> items = originalItems;

        TextField editor = comboBox.getEditor();
        String editText = editor.getText().toLowerCase();
        if (editText.length() == 0) {
            return;
        }
        for (int i = 0; i < items.size(); i++) {
            T item = items.get(i);
            String itemString;
            if (item == null) {
                itemString = "";
            } else {
                itemString = item.toString().toLowerCase();
            }


            if (itemString.startsWith(editText)) {
                try {
                    ListView lv = ((ComboBoxListViewSkin) AutoCompleteComboBoxListener.this.comboBox.getSkin()).getListView();
                    lv.getSelectionModel().clearAndSelect(i);
                    lv.scrollTo(i);
//                    lv.scrollTo(lv.getSelectionModel().getSelectedIndex());
                    found = true;
                    break;
                } catch (Exception ignored) {
                }
            }
        }

        String s = editor.getText();
        if (!found) {
            comboBox.getSelectionModel().clearSelection();
            editor.setText(s);
            editor.end();
        }
    }

    @Override
    public void handle(KeyEvent event) {
        if (event.isControlDown() || event.getCode() == KeyCode.BACK_SPACE ||
                event.getCode() == KeyCode.RIGHT ||
                event.getCode() == KeyCode.LEFT ||
                event.getCode() == KeyCode.DOWN ||
                event.getCode() == KeyCode.UP ||
                event.getCode() == KeyCode.PAGE_DOWN ||
                event.getCode() == KeyCode.PAGE_UP ||
                event.getCode() == KeyCode.DELETE ||
                event.getCode() == KeyCode.HOME ||
                event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.ESCAPE
                || event.getCode() == KeyCode.SHIFT
                || event.getCode() == KeyCode.ENTER
                ) {
            return;
        }

        editChanged();
    }

    private void editChanged() {
        TextField editor = comboBox.getEditor();
//        int unselectedLength = editor.getLength() - editor.getSelectedText().length();
//        System.out.println("keyEvent code: " + event.getCode() + " lastLength: " + lastLength + " unselectedLength: " + unselectedLength);
//        if (lastLength != unselectedLength) {
//            lastLength = unselectedLength;
//        }

        sb.delete(0, sb.length());
        sb.append(editor.getText());
        IndexRange ir = editor.getSelection();
        sb.delete(ir.getStart(), sb.length());
        String unselectedText = sb.toString().toLowerCase();
        if (unselectedText.length() == 0) {
            ListView lv = ((ComboBoxListViewSkin) AutoCompleteComboBoxListener.this.comboBox.getSkin()).getListView();
            lv.getSelectionModel().clearSelection();
            lv.scrollTo(0);
            return;
        }


        ObservableList<T> items = originalItems;
        for (int i = 0; i < items.size(); i++) {
            T item = items.get(i);
            if (item != null) {
                String s = item.toString();
                if (s != null) {
                    if (s.toLowerCase().startsWith(unselectedText)) {
                        ListView lv = ((ComboBoxListViewSkin) comboBox.getSkin()).getListView();
                        lv.getSelectionModel().clearAndSelect(i);
                        lv.scrollTo(i);

                        editor.setText(s);
                        editor.positionCaret(unselectedText.length());
                        editor.selectEnd();

                        break;
                    }
                }
            }

        }
//        comboBox.setVisibleRowCount(Math.min(10, newItems.size()));
        if (!comboBox.showingProperty().get()) {
            comboBox.show();
        }

    }


//    @Override
//    public void handle(KeyEvent event) {
//
//        // this variable is used to bypass the auto complete process if the length is the same.
//        // this occurs if user types fast, the length of textfield will record after the user
//        // has typed after a certain delay.
////        if(event.getCode() == KeyCode.BACK_SPACE) {
////            String t1 = comboBox.getEditor().getText();
////            int l = t1.length();
////            t1 = StringUtils.substring(t1, 0, l - 1);
////            System.out.println("t1 " + t1 + " l " + l);
////            comboBox.getEditor().setText(t1);
////        }
//        int ll = comboBox.getEditor().getLength() - comboBox.getEditor().getSelectedText().length();
//        System.out.println("keyEvent code: " + event.getCode() + " lastLength: " + lastLength + " ll: " + ll);
//        if (lastLength != ll)
//            lastLength = ll;
//
//        if (event.isControlDown() || event.getCode() == KeyCode.BACK_SPACE ||
//                event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT ||
//                event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.HOME ||
//                event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB || event.getCode() == KeyCode.ESCAPE
//                )
//            return;
//
//        IndexRange ir = comboBox.getEditor().getSelection();
//        System.out.println("sb before: " + sb + " ir " + ir);
//        sb.delete(0, sb.length());
//        System.out.println("comboBox.getEditor().getText(): " + comboBox.getEditor().getText());
//        sb.append(comboBox.getEditor().getText());
//        System.out.println("sb after: " + sb);
//        // remove selected string index until end so only unselected text will be recorded
//        try {
//            sb.delete(ir.getStart(), sb.length());
//        } catch (Exception e) {
//        }
//        System.out.println("sb after2: " + sb);
//
//        //ObservableList<T> items = comboBox.getItems();
//        ObservableList<T> items = originalItems;
//        ObservableList<T> newItems = FXCollections.observableArrayList();
//        items.stream().filter(
//                e -> sb.length() == 0 ||
//                        (e != null && e.toString().toLowerCase().startsWith(sb.toString()))
//        ).forEach(newItems::add);
//        for (int i = 0; i < items.size(); i++) {
//            T item = items.get(i);
//            String itemString;
//            if (item == null) {
//                itemString = "";
//            } else {
//                itemString = item.toString().toLowerCase();
//            }
//
//            if (itemString.startsWith(comboBox.getEditor().getText().toLowerCase())) {
////                newItems.add(item);
////                System.out.println("added " + item);
//                try {
//                    comboBox.getEditor().setText(sb.toString() + itemString.substring(sb.toString().length()));
//                } catch (Exception e) {
//                    comboBox.getEditor().setText(sb.toString());
//                }
//                comboBox.getEditor().positionCaret(sb.toString().length());
//                comboBox.getEditor().selectEnd();
//                break;
//            }
//        }
//        itemsUpdateInProgress = true;
//        //comboBox.hide();
//        //comboBox.setItems(newItems);
//        itemsUpdateInProgress = false;
//        comboBox.setVisibleRowCount(Math.min(10, newItems.size()));
//        if (!comboBox.showingProperty().get()) {
//            comboBox.show();
//        }
//    }

//    /*
//     * selectClosestResultBasedOnTextFieldValue() - selects the item and scrolls to it when
//     * the popup is shown.
//     *
//     * parameters:
//     *  affect - true if combobox is clicked to show popup so text and caret position will be readjusted.
//     *  inFocus - true if combobox has focus. If not, programmatically press enter key to add new entry to list.
//     *
//     */
//    private void selectClosestResultBasedOnTextFieldValue(boolean affect, boolean inFocus) {
//        ObservableList<T> items = AutoCompleteComboBoxListener.this.comboBox.getItems();
//        boolean found = false;
//
//        String editText = AutoCompleteComboBoxListener.this.comboBox.getEditor().getText().toLowerCase();
//        for (int i = 0; i < items.size(); i++) {
//            T item = items.get(i);
//            String itemString;
//            if (item == null) {
//                itemString = "";
//            } else {
//                itemString = item.toString().toLowerCase();
//            }
//
//
//            if (editText.equals(itemString)) {
//                try {
//                    ListView lv = ((ComboBoxListViewSkin) AutoCompleteComboBoxListener.this.comboBox.getSkin()).getListView();
//                    lv.getSelectionModel().clearAndSelect(i);
//                    lv.scrollTo(lv.getSelectionModel().getSelectedIndex());
//                    found = true;
//                    break;
//                } catch (Exception ignored) {
//                }
//            }
//        }
//
//        String s = comboBox.getEditor().getText();
//        if (!found && affect) {
//            comboBox.getSelectionModel().clearSelection();
//            comboBox.getEditor().setText(s);
//            comboBox.getEditor().end();
//        }
//
//        if (!inFocus && comboBox.getEditor().getText() != null && comboBox.getEditor().getText().trim().length() > 0) {
//            // press enter key programmatically to have this entry added
//            //KeyEvent ke = KeyEvent.impl_keyEvent(comboBox, KeyCode.ENTER.toString(), KeyCode.ENTER.getName(), KeyCode.ENTER.impl_getCode(), false, false, false, false, KeyEvent.KEY_RELEASED);
//            KeyEvent ke = new KeyEvent(KeyEvent.KEY_RELEASED, KeyCode.ENTER.toString(), KeyCode.ENTER.getName(), KeyCode.ENTER, false, false, false, false);
//            comboBox.fireEvent(ke);
//        }
//    }

}