package by.gto.data;

import by.gto.tools.ConnectionMySql;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingWorker;
import org.apache.log4j.Logger;

/**
 *
 * @author Aleks
 */
public class OwnerDataSW extends SwingWorker {

    private static final Logger log = Logger.getLogger(OwnerDataSW.class);
    private final List<String> listOwner = new ArrayList<>();
    private final List<String> listOwnerUNP = new ArrayList<>();
    private AutoCompleteSupport<String> support;
    private AutoCompleteSupport<String> UNP;
    private final JComboBox<String> name;
    private final JComboBox<Integer> unp;
    private final JCheckBox checkBox;
    private final JLabel label;

    public OwnerDataSW(JComboBox<String> name, JComboBox<Integer> unp, JCheckBox checkBox, JLabel label) {
        this.name = name;
        this.unp = unp;
        this.checkBox = checkBox;
        this.label = label;
    }

    @Override
    protected Void doInBackground() {
        if (checkBox.isSelected()) {
            String Query = "SELECT owner_info.id_owner, UPPER(owner_info.`name`), owner_info.`unp` FROM owner_info GROUP BY owner_info.`name` ORDER BY owner_info.`name`";
            //String Query = "SELECT s_note.`name` FROM s_note";
            try {
                try (Connection conn = ConnectionMySql.getInstance().getConn(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(Query)) {
                    while (rs.next()) {
                        listOwner.add(rs.getString(2).trim());
                        if (rs.getString(3) != null) {
                            listOwnerUNP.add(rs.getString(3).trim());
                        }
                    }
                }
            } catch (SQLException ex) {
                log.error(ex);
            }
        }
//        for (int i = 0; i < 100000; i++) {
//            System.out.println(Math.sqrt(i));
//
//        }
        return null;
    }

    @Override
    protected void done() {
        if (checkBox.isSelected()) {
            name.setEnabled(true);
            unp.setEnabled(true);
            label.setEnabled(true);
            name.getInputContext().selectInputMethod(new Locale("ru", "RU"));
            final EventList<String> owners = GlazedLists.eventList(listOwner);
            final EventList<String> ownersUNP = GlazedLists.eventList(listOwnerUNP);
            if (name.getItemCount() < 1) {
                support = AutoCompleteSupport.install(name, owners);
                support.setFilterMode(TextMatcherEditor.CONTAINS);
            }
            if (unp.getItemCount() < 1) {
                UNP = AutoCompleteSupport.install(unp, ownersUNP);
                UNP.setFilterMode(TextMatcherEditor.CONTAINS);
            }
        } else {
            name.setEnabled(false);
            unp.setEnabled(false);
            label.setEnabled(false);

            //support.uninstall();
            //  UNP.uninstall();
        }
    }
}
