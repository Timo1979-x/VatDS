package by.gto.helpers;

import com.sun.javafx.property.PropertyReference;
import javafx.event.EventHandler;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;

import java.math.BigDecimal;

public class TableViewHelpers
{
    public static <RT> void initBigdecimalColumn(TableColumn<RT, BigDecimal> col, String propertyName) {
        col.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        col.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<BigDecimal>() {
            @Override
            public String toString(BigDecimal object) {
                return object.toPlainString();
            }

            @Override
            public BigDecimal fromString(String string) {
                return new BigDecimal(string);
            }
        }));
        col.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<RT, BigDecimal>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<RT, BigDecimal> t) {
                RT rowValue = t.getRowValue();
                PropertyReference<BigDecimal> tPropertyReference = new PropertyReference<BigDecimal>(rowValue.getClass(), propertyName);
                tPropertyReference.set(rowValue, t.getNewValue());
            }
        });
    }

    public static <RT> void initLongColumn(TableColumn<RT, Long> col, String propertyName) {
        col.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        col.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Long>() {
            @Override
            public String toString(Long object) {
                return object.toString();
            }

            @Override
            public Long fromString(String string) {
                return new Long(string);
            }
        }));
        col.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<RT, Long>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<RT, Long> t) {
                RT rowValue = t.getRowValue();
                PropertyReference<Long> tPropertyReference = new PropertyReference<Long>(rowValue.getClass(), propertyName);
                tPropertyReference.set(rowValue, t.getNewValue());
            }
        });
    }
    public static <RT> void initIntegerColumn(TableColumn<RT, Integer> col, String propertyName) {
        col.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        col.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return object.toString();
            }

            @Override
            public Integer fromString(String string) {
                return new Integer(string);
            }
        }));
        col.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<RT, Integer>>() {
            @Override
            public void handle(TableColumn.CellEditEvent<RT, Integer> t) {
                RT rowValue = t.getRowValue();
                PropertyReference<Integer> tPropertyReference = new PropertyReference<Integer>(rowValue.getClass(), propertyName);
                tPropertyReference.set(rowValue, t.getNewValue());
            }
        });
    }
}
