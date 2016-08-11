package by.gto.btoreport.gui;

import by.gto.tools.ConnectionMySql;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.net.URL;
import java.sql.*;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public final class SettingsVATController implements javafx.fxml.Initializable {
    private static final Logger log = Logger.getLogger(SettingsVATController.class);

    private final ObservableList<VatSetting> data = FXCollections.observableArrayList();
    @FXML
    public TableView<VatSetting> table;
    @FXML
    public TableColumn colYear;
    @FXML
    public TableColumn colRangeBegin;
    @FXML
    public TableColumn colRangeEnd;
    @FXML
    public TextField eYear;
    @FXML
    public TextField eRangeBegin;
    @FXML
    public Button bSave;
    @FXML
    public TextField eRangeEnd;

    private boolean modified;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        table.setEditable(true);


        Callback<TableColumn, TableCell> cellFactory =
                new Callback<TableColumn, TableCell>() {
                    public TableCell call(TableColumn p) {
                        return new EditingCell();
                    }
                };
        colYear.setCellValueFactory(
                new PropertyValueFactory<VatSetting, String>("year"));
        colYear.setCellFactory(cellFactory);
        colYear.setOnEditCommit(
                new EventHandler<CellEditEvent<VatSetting, String>>() {
                    @Override
                    public void handle(CellEditEvent<VatSetting, String> t) {
                        String newValue = StringUtils.trim(t.getNewValue());
                        String oldValue = StringUtils.trim(t.getOldValue());
                        VatSetting vatSetting = t.getTableView().getItems().get(
                                t.getTablePosition().getRow());
                        try {
                            Short.parseShort(newValue);
                            vatSetting.setYear(newValue);
                        } catch (NumberFormatException e) {
                            vatSetting.setYear(oldValue);
                        }
                        colYear.setVisible(false);
                        colYear.setVisible(true);
                    }
                }
        );


        colRangeBegin.setCellValueFactory(
                new PropertyValueFactory<VatSetting, String>("begin"));
        colRangeBegin.setCellFactory(cellFactory);
        colRangeBegin.setOnEditCommit(
                new EventHandler<CellEditEvent<VatSetting, String>>() {
                    @Override
                    public void handle(CellEditEvent<VatSetting, String> t) {
                        String newValue = StringUtils.trim(t.getNewValue());
                        String oldValue = StringUtils.trim(t.getOldValue());
                        VatSetting vatSetting = t.getTableView().getItems().get(
                                t.getTablePosition().getRow());
                        try {
                            Long.parseLong(newValue);
                            vatSetting.setBegin(newValue);
                        } catch (NumberFormatException e) {
                            vatSetting.setBegin(oldValue);
                        }
                        colRangeBegin.setVisible(false);
                        colRangeBegin.setVisible(true);
                    }
                }
        );

        colRangeEnd.setCellValueFactory(
                new PropertyValueFactory<VatSetting, String>("end"));
        colRangeEnd.setCellFactory(cellFactory);
        colRangeEnd.setOnEditCommit(
                new EventHandler<CellEditEvent<VatSetting, String>>() {
                    @Override
                    public void handle(CellEditEvent<VatSetting, String> t) {
                        String newValue = StringUtils.trim(t.getNewValue());
                        String oldValue = StringUtils.trim(t.getOldValue());
                        VatSetting vatSetting = t.getTableView().getItems().get(
                                t.getTablePosition().getRow());
                        try {
                            Long.parseLong(newValue);
                            vatSetting.setEnd(newValue);
                        } catch (NumberFormatException e) {
                            vatSetting.setEnd(oldValue);
                        }
                        colRangeEnd.setVisible(false);
                        colRangeEnd.setVisible(true);
                    }
                }
        );


        String query =
                "SELECT " +
                        "       ovs.`year`,\n" +
                        "       ovs.`begin`,\n" +
                        "       ovs.`end` FROM ei_vat_settings ovs\n" +
                        "  ORDER BY ovs.`year`";
        try {
            try (Connection conn = ConnectionMySql.getInstance().getConn(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(query)) {
                while (rs.next()) {
                    VatSetting vs = new VatSetting(rs.getString(1), rs.getString(2), rs.getString(3));
                    data.add(vs);
                }
            }
        } catch (SQLException ex) {
            MainController.showErrorMessage("Ошибка", ex.getMessage());
            log.error(ex);
        }
        try {
            table.setItems(data);
        } catch (Throwable t) {
            log.error(t.getMessage());
        }
    }

    public void bAddRangeAction(ActionEvent actionEvent) {
        String year = StringUtils.trim(eYear.getText());
        String begin = StringUtils.trim(eRangeBegin.getText());
        String end = StringUtils.trim(eRangeEnd.getText());
        try {
            Short.parseShort(year);
            long lBegin = Long.parseLong(begin);
            long lEnd = Long.parseLong(end);

            if (lBegin > lEnd) {
                MainController.showErrorMessage("Данные некорректны", "Начало больше конца");
                return;
            }

            data.stream().forEach(vatSetting -> {
                if (year.equals(vatSetting.getYear())) {
                    throw new NumberFormatException("Нельзя добавить 2 записи для одного года");
                }
            });
        } catch (NumberFormatException e) {
            MainController.showErrorMessage("Данные некорректны", "Данные некорректны");
        }
        data.add(new VatSetting(year, begin, end));
        eYear.clear();
        eRangeBegin.clear();
        eRangeEnd.clear();
        setModified(true);
    }

    public void bSaveAction(ActionEvent actionEvent) {
        String validateResult = validateData();
        if (!StringUtils.isEmpty(validateResult)) {
            MainController.showErrorMessage("Данные некорректны", validateResult);
            return;
        }

        String query = "INSERT INTO ei_vat_settings (`year`, `begin`, `end`)\n" +
                "  VALUES (?, ?, ?)\n" +
                "ON DUPLICATE KEY UPDATE\n" +
                "`begin` = ?,\n" +
                "`end` = ?";
        StringBuilder rslt = new StringBuilder();
        try {
            try (Connection conn = ConnectionMySql.getInstance().getConn(); PreparedStatement ps = conn.prepareStatement(query)) {
                conn.setAutoCommit(false);
                try (Statement st = conn.createStatement()) {
                    st.executeUpdate("delete from ei_vat_settings");
                }
                data.stream().forEach(vatSetting -> {
                            try {
                                ps.setShort(1, Short.parseShort(vatSetting.getYear()));
                                ps.setLong(2, Long.parseLong(vatSetting.getBegin()));
                                ps.setLong(3, Long.parseLong(vatSetting.getEnd()));
                                ps.setLong(4, Long.parseLong(vatSetting.getBegin()));
                                ps.setLong(5, Long.parseLong(vatSetting.getEnd()));
                                int a = ps.executeUpdate();
                            } catch (SQLException e) {
                                rslt.append("\n").append(e.getMessage());
                            }
                        }
                );
                if (rslt.length() == 0) {
                    conn.commit();
                }
            }
            MainController.showInfoMessage("Успех", "Сохранено успешно");
            setModified(false);
        } catch (SQLException ex) {
            rslt.append("\n").append(ex.getMessage());
            log.error(ex);
        }
        if (rslt.length() > 0) {
            MainController.showErrorMessage("Данные некорректны", rslt.toString());
        }
    }

    private String validateData() {
        final Set<VatSetting> setOfRecords = new HashSet<>();
        final StringBuilder sb = new StringBuilder();
        data.stream().forEach(vatSetting -> {
            setOfRecords.stream().forEach(vatSettingInner -> {
                String year = vatSetting.getYear();
                if (vatSettingInner.getYear().equals(year)) {
                    sb.append("\nДублируется год: ").append(year);
                }
            });
            setOfRecords.add(vatSetting);
            try {
                long begin = Long.parseLong(vatSetting.getBegin());
                long end = Long.parseLong(vatSetting.getEnd());
                Short.parseShort(vatSetting.getYear());
                if (begin > end) {
                    sb.append("\nГод: ").append(vatSetting.getYear())
                            .append(" Начало диапазона больше конца диапазона (")
                            .append(begin).append(" > ").append(end).append(")");
                }
            } catch (NumberFormatException e) {
                sb.append("\nГод: ").append(vatSetting.getYear()).append(" данные не являются числами");
            }
        });
        return sb.toString();
    }

    public void bDeleteAction(ActionEvent actionEvent) {
        int selectedIndex = table.getSelectionModel().getSelectedIndex();
//        MainController.showInfoMessage("", "" + selectedIndex + "\n" +
//                data.get(selectedIndex));

        data.remove(selectedIndex);
        setModified(true);
    }

    public static class VatSetting {
        private String year;
        private String begin;
        private String end;

        public VatSetting(String year, String begin, String end) {
            this.year = year;
            this.begin = begin;
            this.end = end;
        }

        @Override
        public String toString() {
            return "VatSetting{" +
                    "year='" + year + '\'' +
                    ", begin='" + begin + '\'' +
                    ", end='" + end + '\'' +
                    '}';
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getBegin() {
            return begin;
        }

        public void setBegin(String begin) {
            this.begin = begin;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }

    }

    class EditingCell extends TableCell<VatSetting, String> {

        private TextField textField;

        public EditingCell() {
        }

        @Override
        public void commitEdit(String newValue) {
            boolean mod = isEditing();
            super.commitEdit(newValue);
            if (mod) {
                SettingsVATController.this.setModified(true);
            }
        }

        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.selectAll();
            }
        }

        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText((String) getItem());
            setGraphic(null);
        }

        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(null);
                }
            }

        }

        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> arg0,
                                    Boolean arg1, Boolean arg2) {
                    if (!arg2) {
                        commitEdit(textField.getText());
                    }
                }
            });
        }

        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.bSave.setText("Сохранить" + (modified ? " *" : ""));
        this.modified = modified;
    }
}




