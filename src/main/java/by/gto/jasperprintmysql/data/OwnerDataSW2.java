package by.gto.jasperprintmysql.data;

import by.gto.tools.ConnectionMySql;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author Aleks
 */
public class OwnerDataSW2 extends SwingWorker {

    private static final Logger log = Logger.getLogger(OwnerDataSW2.class);
    private final ObservableList<String> listOwner = FXCollections.observableArrayList();
    private final ObservableList<String> listOwnerUNP = FXCollections.observableArrayList();
    private final ComboBox name;
    private final ComboBox unp;
    private final boolean enabled;
    private final Label label;

    /**
     *
     * @param name
     * @param unp
     * @param enabled
     * @param label
     */
    public OwnerDataSW2(ComboBox name, ComboBox unp, boolean enabled, Label label) {
        this.name = name;
        this.unp = unp;
        this.enabled = enabled;
        this.label = label;
    }

    @Override
    protected Void doInBackground() {
        if (enabled) {
            String Query = "SELECT owner_info.id_owner, UPPER(owner_info.`name`), owner_info.`unp` FROM owner_info GROUP BY owner_info.`name` ORDER BY owner_info.`name`";
            try {
                try (Connection conn = ConnectionMySql.getInstance().getConn(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(Query)) {
                    while (rs.next()) {
                        listOwner.add(StringUtils.trim(rs.getString(2)));
                        listOwner.add(StringUtils.trim(rs.getString(2)) + " 1");
                        if (rs.getString(3) != null) {
                            listOwnerUNP.add(rs.getString(3).trim());
                        }
                    }
                }
            } catch (SQLException ex) {
                log.error(ex);
            }
        }
        return null;
    }

    @Override
    protected void done() {
        if (enabled) {
            name.setDisable(false);
            unp.setDisable(false);
            label.setDisable(false);

            name.setItems(listOwner);
//            name.getItems().clear();
//            name.getItems().addAll(listOwner);

            unp.setItems(listOwnerUNP);
//            unp.getItems().clear();
//            unp.getItems().addAll(listOwnerUNP);


//            name.getInputContext().selectInputMethod(new Locale("ru", "RU"));
//            final EventList<String> owners = GlazedLists.eventList(listOwner);
//            final EventList<String> ownersUNP = GlazedLists.eventList(listOwnerUNP);

            //if (name.getItemCount() < 1) {
            if (name.getItems().size() < 1) {
                /*support = AutoCompleteSupport.install(name, owners);
                support.setFilterMode(TextMatcherEditor.CONTAINS);*/
            }
            //if (unp.getItemCount() < 1) {
            if (unp.getItems().size() < 1) {
                /*UNP = AutoCompleteSupport.install(unp, ownersUNP);
                UNP.setFilterMode(TextMatcherEditor.CONTAINS);*/
            }
        } else {
            name.setDisable(true);
            unp.setDisable(true);
            label.setDisable(true);
        }
    }
}
