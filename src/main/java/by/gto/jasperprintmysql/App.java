package by.gto.jasperprintmysql;

import by.gto.tools.ConfigReader;
import by.gto.tools.ConnectionMySql;
import by.gto.tools.ModalFrameUtil;
import com.mysql.jdbc.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UnsupportedLookAndFeelException;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App {

    private static final Logger log = LogManager.getLogger(App.class);

    /**
     *
     * @param title
     * @param message
     */
    public static void showMessage(String title, String message) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JOptionPane.showMessageDialog(frame, message);
    }

    public static void print(LocalDateTime startMonth, LocalDateTime endMonth, String report, List<Integer> owner_type, String owner, String ownerUNP, byte bankTransfer) {
        //report = "report1";

        int result = startMonth.compareTo(endMonth);
        String relationship;

        if (result < 0) {
            relationship = "is earlier than";
            endMonth = endMonth.plusDays(1).minusSeconds(1);
        } else if (result == 0) {
            relationship = "is the same time as";
            endMonth = endMonth.plusDays(1).minusSeconds(1);
        } else {
            relationship = "is later than";
            LocalDateTime dt = startMonth;
            startMonth = endMonth;
            endMonth = dt.plusDays(1).minusSeconds(1);
        }

//        System.out.println(String.format("%s %s %s", startMonth, relationship, endMonth));
        try {
            javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            JasperPrint jasperPrint;
            try (Connection conn = ConnectionMySql.getInstance().getConn();
                    Statement st = conn.createStatement();) {
                Map<String, Object> map = new HashMap<>();
                //  map.put("startMonth", Date.from(startMonth.atZone(ZoneId.systemDefault()).toInstant()));
                map.put("startMonth", startMonth);
//                System.out.println("startMonth.toDate():" + startMonth);
//                map.put("endMonth", Date.from(endMonth.atZone(ZoneId.systemDefault()).toInstant()));
                map.put("endMonth", endMonth);
//                System.out.println("endMonth.toDate():" + endMonth);
                map.put("position", ConfigReader.getInstance().getPosition());
                map.put("chiefDS", ConfigReader.getInstance().getChiefDS());
                map.put("owner_type", owner_type);
                map.put("NDS", ConfigReader.getInstance().getNDS());
                map.put("bankTransfer", bankTransfer);

                String sqlParam = " ";
                if (owner != null) {
                    sqlParam += MessageFormat.format("and o.name like \"%{0}%\"", owner);
                    System.out.println("sqlParam: " + sqlParam);
                    //sqlParam += "and o.name like \"%" + owner + "%\"";
                }

                if (ownerUNP != null) {
                    sqlParam += String.format("and o.unp=%s", ownerUNP);
                }

                map.put("test", sqlParam);

                Iterator<Integer> it = owner_type.iterator();
                StringBuilder sb = new StringBuilder();
                while (it.hasNext()) {
                    sb.append(it.next());
                    if (it.hasNext()) {
                        sb.append(",");
                    }
                }

                String Query = null;
                if (owner != null) {
                    Query = String.format("select count(dk.id_blanc) total,(select count(dk2.id_blanc) from blanc_ts_info dk2 left join `to`.blanc b on dk2.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=dk2.id_ts_info left join `to`.owner_info o on o.id_owner=i.id_owner_sobs where b.id_blanc_status=2 and b.id_blanc_type=1 and dk2.date_ot between '%s' and '%s' and dk2.id_blanc_repeat is null and o.id_owner_type in (%s) and o.name = \"%s\") good, (select count(dk2.id_blanc) from blanc_ts_info dk2 left join `to`.blanc b on dk2.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=dk2.id_ts_info left join `to`.owner_info o on o.id_owner=i.id_owner_sobs where b.id_blanc_status=2 and b.id_blanc_type=1 and dk2.date_ot between '%s' and '%s' and dk2.id_blanc_repeat is not null and o.id_owner_type in (%s) and o.name = \"%s\") good2 from blanc_ts_info dk left join `to`.blanc b on dk.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=dk.id_ts_info left join `to`.owner_info o on o.id_owner=i.id_owner_sobs WHERE b.id_blanc_status=2 and b.id_blanc_type=1 and dk.`date_ot` BETWEEN '%s' and '%s' and o.id_owner_type in (%s) and o.name = \"%s\"", startMonth.toString(), endMonth.toString(), sb.toString(), owner, startMonth.toString(), endMonth.toString(), sb.toString(), owner, startMonth.toString(), endMonth.toString(), sb.toString(), owner);
                }
                if (ownerUNP != null) {
                    Query = String.format("select count(dk.id_blanc) total,(select count(dk2.id_blanc) from blanc_ts_info dk2 left join `to`.blanc b on dk2.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=dk2.id_ts_info left join `to`.owner_info o on o.id_owner=i.id_owner_sobs where b.id_blanc_status=2 and b.id_blanc_type=1 and dk2.date_ot between '%s' and '%s' and dk2.id_blanc_repeat is null and o.id_owner_type in (%s) and o.unp = \"%s\") good, (select count(dk2.id_blanc) from blanc_ts_info dk2 left join `to`.blanc b on dk2.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=dk2.id_ts_info left join `to`.owner_info o on o.id_owner=i.id_owner_sobs where b.id_blanc_status=2 and b.id_blanc_type=1 and dk2.date_ot between '%s' and '%s' and dk2.id_blanc_repeat is not null and o.id_owner_type in (%s) and o.unp = \"%s\") good2 from blanc_ts_info dk left join `to`.blanc b on dk.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=dk.id_ts_info left join `to`.owner_info o on o.id_owner=i.id_owner_sobs WHERE b.id_blanc_status=2 and b.id_blanc_type=1 and dk.`date_ot` BETWEEN '%s' and '%s' and o.id_owner_type in (%s) and o.unp = \"%s\"", startMonth.toString(), endMonth.toString(), sb.toString(), ownerUNP, startMonth.toString(), endMonth.toString(), sb.toString(), ownerUNP, startMonth.toString(), endMonth.toString(), sb.toString(), ownerUNP);
                }
                if (ownerUNP != null && owner != null) {
                    Query = String.format("select count(dk.id_blanc) total,(select count(dk2.id_blanc) from blanc_ts_info dk2 left join `to`.blanc b on dk2.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=dk2.id_ts_info left join `to`.owner_info o on o.id_owner=i.id_owner_sobs where b.id_blanc_status=2 and b.id_blanc_type=1 and dk2.date_ot between '%s' and '%s' and dk2.id_blanc_repeat is null and o.id_owner_type in (%s) and o.unp = \"%s\" and o.name = \"%s\") good, (select count(dk2.id_blanc) from blanc_ts_info dk2 left join `to`.blanc b on dk2.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=dk2.id_ts_info left join `to`.owner_info o on o.id_owner=i.id_owner_sobs where b.id_blanc_status=2 and b.id_blanc_type=1 and dk2.date_ot between '%s' and '%s' and dk2.id_blanc_repeat is not null and o.id_owner_type in (%s) and o.unp = \"%s\" and o.name = \"%s\") good2 from blanc_ts_info dk left join `to`.blanc b on dk.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=dk.id_ts_info left join `to`.owner_info o on o.id_owner=i.id_owner_sobs WHERE b.id_blanc_status=2 and b.id_blanc_type=1 and dk.`date_ot` BETWEEN '%s' and '%s' and o.id_owner_type in (%s) and o.unp = \"%s\" and o.name = \"%s\"", startMonth.toString(), endMonth.toString(), sb.toString(), ownerUNP, owner, startMonth.toString(), endMonth.toString(), sb.toString(), ownerUNP, owner, startMonth.toString(), endMonth.toString(), sb.toString(), ownerUNP, owner);
                }
                if (Query == null) {
                    Query = String.format("select count(dk.id_blanc) total,(select count(dk2.id_blanc) from blanc_ts_info dk2 left join `to`.blanc b on dk2.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=dk2.id_ts_info left join `to`.owner_info o on o.id_owner=i.id_owner_sobs where b.id_blanc_status=2 and b.id_blanc_type=1 and dk2.date_ot between '%s' and '%s' and dk2.id_blanc_repeat is null and o.id_owner_type in (%s)) good, (select count(dk2.id_blanc) from blanc_ts_info dk2 left join `to`.blanc b on dk2.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=dk2.id_ts_info left join `to`.owner_info o on o.id_owner=i.id_owner_sobs where b.id_blanc_status=2 and b.id_blanc_type=1 and dk2.date_ot between '%s' and '%s' and dk2.id_blanc_repeat is not null and o.id_owner_type in (%s)) good2 from blanc_ts_info dk left join `to`.blanc b on dk.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=dk.id_ts_info left join `to`.owner_info o on o.id_owner=i.id_owner_sobs WHERE b.id_blanc_status=2 and b.id_blanc_type=1 and dk.`date_ot` BETWEEN '%s' and '%s' and o.id_owner_type in (%s)", startMonth.toString(), endMonth.toString(), sb.toString(), startMonth.toString(), endMonth.toString(), sb.toString(), startMonth.toString(), endMonth.toString(), sb.toString());
                }
                ResultSet rs = st.executeQuery(Query);
                while (rs.next()) { //обязательная проверка, в начальном состоянии вылетит Exception при взятии какого-либо значения
                    map.put("total", rs.getInt(1)); // нумерация столбцов начинается с 1
                    map.put("good", rs.getInt(2));
                    map.put("good2", rs.getInt(3));
                }
                rs = st.executeQuery("SELECT s_ds.num_ds FROM `s_ds` WHERE `s_ds`.`Valid`=1;");
                while (rs.next()) {
                    map.put("num_ds", rs.getInt(1));
                }
                rs.close();

//                StringBuilder stringBuilder = new StringBuilder();
//                stringBuilder.append("SELECT DATE_FORMAT(bi.date_ot, '%d.%m.%y') 'Дата', concat(b.seria,' №', LPAD(b.number, 7, 0)) 'Серия, номер', o.name 'Собственник', `oi`.`name` 'Заказчик', `oi`.`unp` 'УНП', ROUND((tar.summa_oplaty/1.");
//                stringBuilder.append(ConfigReader.getInstance().getNDS());
//                stringBuilder.append("),0) 'Услуги без НДС', ROUND((tar.summa_oplaty-(tar.summa_oplaty/1.");
//                stringBuilder.append(ConfigReader.getInstance().getNDS());
//                stringBuilder.append(")),0) 'НДС', tar.summa_oplaty 'Всего с НДС' FROM `to`.blanc_ts_info bi left join `to`.blanc b on bi.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=bi.id_ts_info left join `to`.sd_tarifs_ts_info tar on tar.id_ts_info=bi.id_ts_info and tar.id_blanc=bi.id_blanc left join `to`.owner_info o on o.id_owner=i.id_owner_sobs LEFT JOIN `owner_info` AS `oi` ON `oi`.`id_owner` = `i`.`id_owner_zakazch` WHERE bi.date_ot BETWEEN '");
//                stringBuilder.append(startMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//                stringBuilder.append("' and '");
//                stringBuilder.append(endMonth.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
//                stringBuilder.append("' and b.id_blanc_status=2 and b.id_blanc_type=1 AND `tar`.`bank_transfer` = ");
//                stringBuilder.append(bankTransfer);
//                stringBuilder.append(" ORDER BY bi.date_ot;");
//                System.out.println("Query: " + stringBuilder);
                // ResultSet rsTest = st.executeQuery(stringBuilder.toString());
                // JRDataSource jrDataSource = new JRResultSetDataSource(rsTest);
                //  map.put("TableDataSource", jrDataSource);
                // jasperPrint = JasperFillManager.fillReport(String.format("reports/%s.jasper", report), map, jrDataSource);
                //  rsTest.close();
                // String printFileName = JasperFillManager.fillReportToFile(String.format("reports/%s.jasper", report), map, conn);
//                JasperExportManager.exportReportToPdfFile(printFileName, "D:\\test\\1.pdf");
                jasperPrint = JasperFillManager.fillReport(String.format("reports/%s.jasper", report), map, conn);
            }

            MyViewer myViewer = new MyViewer(jasperPrint, false, " pdf, rtf, multipleXLS, singleXLS, csv, xml");
            myViewer.setTitle("Предварительный просмотр");
            myViewer.setExtendedState(myViewer.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            if (!jasperPrint.getPages().isEmpty()) {
                ModalFrameUtil.showAsModal(myViewer, MainView.getF(), JFrame.MAXIMIZED_BOTH);
                MainView.getF().toFront();
            }
        } catch (JRException | SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            showMessage("jasper JRException", ex.toString());
            log.error(ex);
        }
    }

    private App() {
    }
}
