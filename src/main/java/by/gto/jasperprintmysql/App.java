package by.gto.jasperprintmysql;

import by.gto.tools.ConfigReader;
import by.gto.tools.ConnectionMySql;
import by.gto.tools.ModalFrameUtil;
import com.mysql.jdbc.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
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
import org.joda.time.DateTime;

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

    public static void print(DateTime startMonth, DateTime endMonth, String report, List<Integer> owner_type, String owner, String ownerUNP, byte bankTransfer) {
        DateTime dt;
        report = "report1";
        //// from Joda to JDK
        //   DateTime dt= new DateTime();
//        dt = startMonth.withZone(DateTimeZone.forID("Europe/Minsk"));
//        startMonth = dt;
//        dt = endMonth.withZone(DateTimeZone.forID("Europe/Minsk"));
//        endMonth = dt;
//    Date jdkDate = dt.toDate();
//
//    // from JDK to Joda
//    dt = new DateTime(jdkDate);
//  
        int result = startMonth.compareTo(endMonth);
        String relationship;

        if (result < 0) {
            relationship = "is earlier than";
            startMonth = startMonth.dayOfMonth().roundFloorCopy();
            endMonth = endMonth.millisOfDay().setCopy(1).plusDays(1).minusSeconds(1);
        } else if (result == 0) {
            relationship = "is the same time as";
            startMonth = startMonth.dayOfMonth().roundFloorCopy();
            endMonth = endMonth.millisOfDay().setCopy(1).plusDays(1).minusSeconds(1);
        } else {
            relationship = "is later than";
            dt = startMonth;
            startMonth = endMonth.dayOfMonth().roundFloorCopy();
            endMonth = dt.millisOfDay().setCopy(1).plusDays(1).minusSeconds(1);

        }
        System.out.println(String.format("%s %s %s", startMonth, relationship, endMonth));
        try {
            javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            JasperPrint jasperPrint;
            try (Connection conn = ConnectionMySql.getInstance().getConn();
                    Statement st = conn.createStatement();) {
                Map<String, Object> map = new HashMap<>();
                map.put("startMonth", startMonth.toDate());
                System.out.println("startMonth.toDate():" + startMonth.toDate());
                map.put("endMonth", endMonth.toDate());
                System.out.println("endMonth.toDate():" + endMonth.toDate());
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
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("SELECT DATE_FORMAT(bi.date_ot, '%d.%m.%y') 'Дата', concat(b.seria,' №', LPAD(b.number, 7, 0)) 'Серия, номер', o.name 'Собственник', `oi`.`name` 'Заказчик', `oi`.`unp` 'УНП', ROUND((tar.summa_oplaty/1.");
                stringBuilder.append(ConfigReader.getInstance().getNDS());
                stringBuilder.append("),0) 'Услуги без НДС', ROUND((tar.summa_oplaty-(tar.summa_oplaty/1.");
                stringBuilder.append(ConfigReader.getInstance().getNDS());
                stringBuilder.append(")),0) 'НДС', tar.summa_oplaty 'Всего с НДС' FROM `to`.blanc_ts_info bi left join `to`.blanc b on bi.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=bi.id_ts_info left join `to`.sd_tarifs_ts_info tar on tar.id_ts_info=bi.id_ts_info and tar.id_blanc=bi.id_blanc left join `to`.owner_info o on o.id_owner=i.id_owner_sobs LEFT JOIN `owner_info` AS `oi` ON `oi`.`id_owner` = `i`.`id_owner_zakazch` WHERE bi.date_ot BETWEEN '");
                stringBuilder.append(startMonth.toString("yyyy-MM-dd HH:mm:ss"));
                stringBuilder.append("' and '");
                stringBuilder.append(endMonth.toString("yyyy-MM-dd HH:mm:ss"));
                stringBuilder.append("' and b.id_blanc_status=2 and b.id_blanc_type=1 AND `tar`.`bank_transfer` = ");
                stringBuilder.append(bankTransfer);
                stringBuilder.append(" ORDER BY bi.date_ot;");
                System.out.println("Query: " + stringBuilder);
                ResultSet rsTest = st.executeQuery(stringBuilder.toString());
                JRDataSource jrDataSource = new JRResultSetDataSource(rsTest);
                jasperPrint = JasperFillManager.fillReport(String.format("reports/%s.jasper", report), map, jrDataSource);
                rsTest.close();

//                String printFileName = JasperFillManager.fillReportToFile(String.format("reports/%s.jasper", report),
//                        map, conn);
//                JasperExportManager.exportReportToPdfFile(printFileName, "D:\\test\\1.pdf");
                //jasperPrint = JasperFillManager.fillReport(String.format("reports/%s.jasper", report), map, conn);
            }
//            JasperViewer jViewer = new JasperViewer(jasperPrint);
//            jViewer.setTitle("Предварительный просмотр");
//            jViewer.isMaximumSizeSet();
//            jViewer.isVisible();
            //   jViewer.isMaximumSizeSet();
            //   jViewer.setVisible(true);
            //JasperViewer.MAXIMIZED_BOTH;
            //    JRViewer jv = new JRViewer(jasperPrint);
            //   JasperViewer.viewReport(jasperPrint);
//            JFrame frame = new JFrame("тест");
//            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
//            

//            JDialog dialog = new JDialog();
//            dialog.setTitle("Предварительный просмотр");
//            dialog.setModal(true);
//            dialog.setResizable(true);
//            dialog.setLocationByPlatform(true);
            //JRViewer JV = new JRViewer(jasperPrint);
            MyViewer myViewer = new MyViewer(jasperPrint, false, " pdf, rtf, multipleXLS, singleXLS, csv, xml");

            myViewer.setTitle("Предварительный просмотр");
            myViewer.setExtendedState(myViewer.getExtendedState() | JFrame.MAXIMIZED_BOTH);
//            //   myViewer.setVisible(true);
//            System.out.println("NORMAL" + JFrame.NORMAL);
//            System.out.println("MAXIMIZED_BOTH" + JFrame.MAXIMIZED_BOTH);
            if (!jasperPrint.getPages().isEmpty()) {
                ModalFrameUtil.showAsModal(myViewer, MainView.getF(), JFrame.MAXIMIZED_BOTH);
                MainView.getF().toFront();
                // new MainView().toFront();
//                final Toolkit toolkit = Toolkit.getDefaultToolkit();
//                final Dimension screenSize = toolkit.getScreenSize();
//                dialog.setMaximumSize(new java.awt.Dimension(screenSize.width, screenSize.height));
//                dialog.setMinimumSize(new java.awt.Dimension(screenSize.width, screenSize.height - 40));
//                final int x = (screenSize.width - dialog.getWidth()) / 2;
//                final int y = (screenSize.height - dialog.getHeight() - 40) / 2;
//                dialog.setLocation(x, y);
//                dialog.setPreferredSize(null);
//                dialog.add(JV);
//                dialog.setVisible(true);
            }

//            JasperViewer jV = new JasperViewer(jasperPrint, false);
//            jV.setTitle("Предварительный просмотр");
//            jV.setExtendedState(jV.getExtendedState() | JFrame.MAXIMIZED_BOTH);
//
//            //На весь экран
//            //  jV.setExtendedState(Frame.MAXIMIZED_BOTH);
////            frame.add(JV);
////            frame.setVisible(true);
//
//            if (!jasperPrint.getPages().isEmpty()) {
//                ModalFrameUtil.showAsModal(jV, MainView.getF());
//                MainView.getF().toFront();
//                // new MainView().toFront();
////                final Toolkit toolkit = Toolkit.getDefaultToolkit();
////                final Dimension screenSize = toolkit.getScreenSize();
////                dialog.setMaximumSize(new java.awt.Dimension(screenSize.width, screenSize.height));
////                dialog.setMinimumSize(new java.awt.Dimension(screenSize.width, screenSize.height - 40));
////                final int x = (screenSize.width - dialog.getWidth()) / 2;
////                final int y = (screenSize.height - dialog.getHeight() - 40) / 2;
////                dialog.setLocation(x, y);
////                dialog.setPreferredSize(null);
////                dialog.add(JV);
////                dialog.setVisible(true);
//            }
            //      JFrame fr = new JFrame("Предварительный просмотр");
            //   JFrame fr = new JFrame("Предварительный просмотр");
            //  JasperViewer.viewReport(jasperPrint);
            //       JRViewer JV = new JRViewer(jasperPrint);
            // JRViewer JV = new JRViewer("reports/UnicodeReport.jrprint", false);
            //            JFrame fr = new JFrame("UnicodeReport");
            //        fr.setExtendedState(JFrame.MAXIMIZED_BOTH);
            //    fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //        fr.add(JV);
            //          fr.add(JV);
            //          fr.pack();
            //          fr.setVisible(true);
        } catch (JRException | SQLException | ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            showMessage("jasper JRException", ex.toString());
            log.error(ex);
        }
    }

    private App() {
    }
}
