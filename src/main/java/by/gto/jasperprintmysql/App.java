package by.gto.jasperprintmysql;

import by.gto.btoreport.gui.MainController;
import by.gto.tools.ConfigReader;
import by.gto.tools.ConnectionMySql;
import com.mgrecol.jasper.jasperviewerfx.JRViewerFx;
import com.mgrecol.jasper.jasperviewerfx.JRViewerFxMode;
import com.mysql.jdbc.Connection;
import com.mysql.jdbc.StringUtils;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class App {
    private final static Logger log = Logger.getLogger(App.class);

    public static void print(LocalDateTime fromDate, LocalDateTime beforeDate, String report, List<Integer> owner_type, String owner, String ownerUNP, byte bankTransfer) {
        //   report = "report1";

        int result = fromDate.compareTo(beforeDate);
//        String relationship;

        if (result < 0) {
//            relationship = "is earlier than";
            //beforeDate = beforeDate.plusDays(1).minusSeconds(1);
        } else if (result == 0) {
//            relationship = "is the same time as";
           // beforeDate = beforeDate.plusDays(1).minusSeconds(1);
        } else {
//            relationship = "is later than";
            LocalDateTime dt = fromDate;
            fromDate = beforeDate.minusDays(1).plusSeconds(1);
            beforeDate = dt.plusDays(1).minusSeconds(1);
        }

//        System.out.println(String.format("%s %s %s", startMonth, relationship, endMonth));
        try {
            JasperPrint jasperPrint;
            try (Connection conn = ConnectionMySql.getInstance().getConn();
                 Statement st = conn.createStatement();) {
                Map<String, Object> map = new HashMap<>();
                map.put("fromDate", fromDate);
                map.put("beforeDate", beforeDate);
                map.put("position", ConfigReader.getInstance().getPosition());
                map.put("chiefDS", ConfigReader.getInstance().getChiefDS());
                map.put("owner_type", owner_type);
                map.put("NDS", ConfigReader.getInstance().getNDS());
                map.put("bankTransfer", bankTransfer);
                map.put("version", Version.getVERSION() + " от " + Version.getDATEBUILD());

                Iterator<Integer> it = owner_type.iterator();
                StringBuilder sb = new StringBuilder();
                while (it.hasNext()) {
                    sb.append(it.next());
                    if (it.hasNext()) {
                        sb.append(",");
                    }
                }

                try (ResultSet rs = st.executeQuery("SELECT s_ds.num_ds FROM `s_ds` WHERE `s_ds`.`Valid`=1;")) {
                    while (rs.next()) {
                        map.put("num_ds", rs.getInt(1));
                    }
                }

                StringBuilder stringBuilder;

                switch (report) {
                    case "corporatePerson": {
                        stringBuilder = CorporatePersonQuery();
                    }
                    break;
                    case "forBTO": {
                        stringBuilder = ForBTOQuery(fromDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), beforeDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    }
                    break;
                    case "forDS210": {
                        stringBuilder = ActiveListQuery();
                    }
                    break;
                    case "forSlutsk": {
                        stringBuilder = ForSlutskQuery();
                        String Query = null;
                        if (owner != null) {
                            Query = String.format("select count(dk.id_blanc) total,(select count(dk2.id_blanc) from blanc_ts_info dk2 left join `to`.blanc b on dk2.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=dk2.id_ts_info left join `to`.owner_info o on o.id_owner=i.id_owner_sobs where b.id_blanc_status=2 and b.id_blanc_type=1 and dk2.date_ot between '%s' and '%s' and dk2.id_blanc_repeat is null and o.id_owner_type in (%s) and o.name = \"%s\") good, (select count(dk2.id_blanc) from blanc_ts_info dk2 left join `to`.blanc b on dk2.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=dk2.id_ts_info left join `to`.owner_info o on o.id_owner=i.id_owner_sobs where b.id_blanc_status=2 and b.id_blanc_type=1 and dk2.date_ot between '%s' and '%s' and dk2.id_blanc_repeat is not null and o.id_owner_type in (%s) and o.name = \"%s\") good2 from blanc_ts_info dk left join `to`.blanc b on dk.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=dk.id_ts_info left join `to`.owner_info o on o.id_owner=i.id_owner_sobs WHERE b.id_blanc_status=2 and b.id_blanc_type=1 and dk.`date_ot` BETWEEN '%s' and '%s' and o.id_owner_type in (%s) and o.name = \"%s\"", fromDate.toString(), beforeDate.toString(), sb.toString(), owner, fromDate.toString(), beforeDate.toString(), sb.toString(), owner, fromDate.toString(), beforeDate.toString(), sb.toString(), owner);
                        }
                        if (ownerUNP != null) {
                            Query = String.format("select count(dk.id_blanc) total,(select count(dk2.id_blanc) from blanc_ts_info dk2 left join `to`.blanc b on dk2.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=dk2.id_ts_info left join `to`.owner_info o on o.id_owner=i.id_owner_sobs where b.id_blanc_status=2 and b.id_blanc_type=1 and dk2.date_ot between '%s' and '%s' and dk2.id_blanc_repeat is null and o.id_owner_type in (%s) and o.unp = \"%s\") good, (select count(dk2.id_blanc) from blanc_ts_info dk2 left join `to`.blanc b on dk2.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=dk2.id_ts_info left join `to`.owner_info o on o.id_owner=i.id_owner_sobs where b.id_blanc_status=2 and b.id_blanc_type=1 and dk2.date_ot between '%s' and '%s' and dk2.id_blanc_repeat is not null and o.id_owner_type in (%s) and o.unp = \"%s\") good2 from blanc_ts_info dk left join `to`.blanc b on dk.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=dk.id_ts_info left join `to`.owner_info o on o.id_owner=i.id_owner_sobs WHERE b.id_blanc_status=2 and b.id_blanc_type=1 and dk.`date_ot` BETWEEN '%s' and '%s' and o.id_owner_type in (%s) and o.unp = \"%s\"", fromDate.toString(), beforeDate.toString(), sb.toString(), ownerUNP, fromDate.toString(), beforeDate.toString(), sb.toString(), ownerUNP, fromDate.toString(), beforeDate.toString(), sb.toString(), ownerUNP);
                        }
                        if (ownerUNP != null && owner != null) {
                            Query = String.format("select count(dk.id_blanc) total,(select count(dk2.id_blanc) from blanc_ts_info dk2 left join `to`.blanc b on dk2.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=dk2.id_ts_info left join `to`.owner_info o on o.id_owner=i.id_owner_sobs where b.id_blanc_status=2 and b.id_blanc_type=1 and dk2.date_ot between '%s' and '%s' and dk2.id_blanc_repeat is null and o.id_owner_type in (%s) and o.unp = \"%s\" and o.name = \"%s\") good, (select count(dk2.id_blanc) from blanc_ts_info dk2 left join `to`.blanc b on dk2.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=dk2.id_ts_info left join `to`.owner_info o on o.id_owner=i.id_owner_sobs where b.id_blanc_status=2 and b.id_blanc_type=1 and dk2.date_ot between '%s' and '%s' and dk2.id_blanc_repeat is not null and o.id_owner_type in (%s) and o.unp = \"%s\" and o.name = \"%s\") good2 from blanc_ts_info dk left join `to`.blanc b on dk.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=dk.id_ts_info left join `to`.owner_info o on o.id_owner=i.id_owner_sobs WHERE b.id_blanc_status=2 and b.id_blanc_type=1 and dk.`date_ot` BETWEEN '%s' and '%s' and o.id_owner_type in (%s) and o.unp = \"%s\" and o.name = \"%s\"", fromDate.toString(), beforeDate.toString(), sb.toString(), ownerUNP, owner, fromDate.toString(), beforeDate.toString(), sb.toString(), ownerUNP, owner, fromDate.toString(), beforeDate.toString(), sb.toString(), ownerUNP, owner);
                        }
                        if (Query == null) {
                            Query = String.format("select count(dk.id_blanc) total,(select count(dk2.id_blanc) from blanc_ts_info dk2 left join `to`.blanc b on dk2.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=dk2.id_ts_info left join `to`.owner_info o on o.id_owner=i.id_owner_sobs where b.id_blanc_status=2 and b.id_blanc_type=1 and dk2.date_ot between '%s' and '%s' and dk2.id_blanc_repeat is null and o.id_owner_type in (%s)) good, (select count(dk2.id_blanc) from blanc_ts_info dk2 left join `to`.blanc b on dk2.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=dk2.id_ts_info left join `to`.owner_info o on o.id_owner=i.id_owner_sobs where b.id_blanc_status=2 and b.id_blanc_type=1 and dk2.date_ot between '%s' and '%s' and dk2.id_blanc_repeat is not null and o.id_owner_type in (%s)) good2 from blanc_ts_info dk left join `to`.blanc b on dk.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=dk.id_ts_info left join `to`.owner_info o on o.id_owner=i.id_owner_sobs WHERE b.id_blanc_status=2 and b.id_blanc_type=1 and dk.`date_ot` BETWEEN '%s' and '%s' and o.id_owner_type in (%s)", fromDate.toString(), beforeDate.toString(), sb.toString(), fromDate.toString(), beforeDate.toString(), sb.toString(), fromDate.toString(), beforeDate.toString(), sb.toString());
                        }

                        try (ResultSet resultSet = st.executeQuery(Query)) {
                            while (resultSet.next()) { //обязательная проверка, в начальном состоянии вылетит Exception при взятии какого-либо значения
                                map.put("total", resultSet.getInt(1)); // нумерация столбцов начинается с 1
                                map.put("good", resultSet.getInt(2));
                                map.put("good2", resultSet.getInt(3));
                            }
                        }
                    }
                    break;
                    case "listIndividual": {
                        stringBuilder = ListIndividualQuery();
                    }
                    break;
                    case "OrderByTariff": {
                        stringBuilder = OrderByTariffQuery();
                    }
                    break;
                    case "recordBook": {
                        stringBuilder = RecordBookQuery();
                    }
                    break;
                    default: {
                        stringBuilder = new StringBuilder();
                    }
                    break;
                }
                if (!report.equals("forBTO")) {
                    if (!report.equals("forDS210")) {
                        stringBuilder.append(fromDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        stringBuilder.append("' and '");
                        stringBuilder.append(beforeDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                        stringBuilder.append("' and b.id_blanc_status=2 and b.id_blanc_type=1");
                    }

                    if (bankTransfer != 2) {
                        stringBuilder.append(" AND `tar`.`bank_transfer` = ");
                        stringBuilder.append(bankTransfer);
                    }

                    stringBuilder.append(" and o.id_owner_type in (").append(sb).append(") ");

                    if (!StringUtils.isNullOrEmpty(owner)) {
                        stringBuilder.append(" and o.name like \"%").append(owner).append("%\"");
                    }

                    if (!StringUtils.isNullOrEmpty(ownerUNP)) {
                        stringBuilder.append(" and o.unp=").append(ownerUNP);
                    }

                    if (report.equals("OrderByTariff")) {
                        stringBuilder.append(" GROUP BY `sttt`.`name`");
                    }

                    if (report.equals("forDS210")) {
                        stringBuilder.append(" ORDER BY r.`name`");
                    } else {
                        stringBuilder.append(" ORDER BY bi.date_ot;");
                    }
                }

                log.info("Query: " + stringBuilder);
                ResultSet rsTest = st.executeQuery(stringBuilder.toString());
                log.info("rsTest");
                //  map.put("TableDataSource", jrDataSource);
                // jasperPrint = JasperFillManager.fillReport(String.format("reports/%s.jasper", report), map, jrDataSource);
                //  rsTest.close();
                // String printFileName = JasperFillManager.fillReportToFile(String.format("reports/%s.jasper", report), map, conn);
//                JasperExportManager.exportReportToPdfFile(printFileName, "D:\\test\\1.pdf");
                try (InputStream inputStream = App.class.getClassLoader().getResourceAsStream(String.format("reports/%s.jasper", report));) {
                    log.info("fillReport1");
                    try {
                        jasperPrint = JasperFillManager.fillReport(inputStream, map, new JRResultSetDataSource(rsTest));
                        log.info("fillReport2");
//                    jasperPrint = JasperFillManager.fillReport(inputStream, map, conn);

                        //jasperPrint = JasperFillManager.fillReport(String.format("reports/%s.jasper", report), map, conn);
                        //MyViewer myViewer = new MyViewer(jasperPrint, false, " pdf, rtf, multipleXLS, singleXLS, csv, xml");
                        log.info("1");
                        //myViewer.setCaption("Предварительный просмотр");
                        log.info("2");
                        //myViewer.setExtendedState(myViewer.getExtendedState() | JFrame.MAXIMIZED_BOTH);
                        log.info("3");
                        if (!jasperPrint.getPages().isEmpty()) {
                            //Stage stage = Main.getStage();
                            Stage stage = new Stage();
                            log.info("4");
                            JRViewerFx viewer = new JRViewerFx(jasperPrint, JRViewerFxMode.REPORT_VIEW, stage);
                            //viewer.start(stage);
                            //ModalFrameUtil.showAsModal(myViewer, MainView.getF(), JFrame.MAXIMIZED_BOTH);
                            //ModalFrameUtil.showAsModalFX(myViewer, Main.getStage(), JFrame.MAXIMIZED_BOTH);
                            log.info("5");
                            //MainView.getF().toFront();
                        }

                    } catch (JRException | ExceptionInInitializerError e) {
                        MainController.showErrorMessage("Ошибка", "Ошибка подключения к серверу.");
                        log.error(e.getMessage(), e);
                    } catch (Exception e) {
                        MainController.showErrorMessage("Ошибка", "Нифига не понятная ошибка");
                        log.error(e.getMessage(),e);
                    }
                }
            }
        } catch (IOException | SQLException | NullPointerException ex) {
            MainController.showErrorMessage("jasper JRException", ex.toString());
            log.error(ex.getMessage(), ex);
        }
    }

    private static StringBuilder CorporatePersonQuery() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT DATE_FORMAT(bi.date_ot, '%d.%m.%y') 'Дата', concat(b.seria,' №', LPAD(b.number, 7, 0)) 'Серия, номер', o.name 'Собственник', `oi`.`name` 'Заказчик', `oi`.`unp` 'УНП', ROUND((tar.summa_oplaty/1.");
        stringBuilder.append(ConfigReader.getInstance().getNDS());
        stringBuilder.append("),2) 'Услуги без НДС', ROUND((tar.summa_oplaty-(tar.summa_oplaty/1.");
        stringBuilder.append(ConfigReader.getInstance().getNDS());
        stringBuilder.append(")),2) 'НДС', tar.summa_oplaty 'Всего с НДС' FROM `to`.blanc_ts_info bi left join `to`.blanc b on bi.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=bi.id_ts_info left join `to`.sd_tarifs_ts_info tar on tar.id_ts_info=bi.id_ts_info and tar.id_blanc=bi.id_blanc left join `to`.owner_info o on o.id_owner=i.id_owner_sobs LEFT JOIN `owner_info` AS `oi` ON `oi`.`id_owner` = `i`.`id_owner_zakazch` WHERE bi.date_ot BETWEEN '");
        return stringBuilder;
    }

    private static StringBuilder RecordBookQuery() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT bi.date_ot 'Дата', o.name 'Собственник', z.name 'Заказчик', m.name 'Марка/модель', r.name 'Рег.знак', concat(b.seria,' №', LPAD(b.number,7,0)) 'Серия, номер', tar.summa_oplaty 'Сумма оплаты' FROM `to`.blanc_ts_info bi left join `to`.blanc b on bi.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=bi.id_ts_info left join `to`.reg_number r on r.id_reg_number=i.id_reg_number left join `to`.s_ts_model m on m.id_ts_model=i.id_ts_marca left join `to`.s_ts_categ c on c.id_ts_categ=i.id_ts_categ left join `to`.sd_tarifs_ts_info tar on tar.id_ts_info=bi.id_ts_info and tar.id_blanc=bi.id_blanc left join `to`.owner_info o on o.id_owner=i.id_owner_sobs left join `to`.owner_info z on z.id_owner=i.id_owner_zakazch left join `to`.s_conclusion con on con.id_conclusion=bi.id_conclusion WHERE bi.date_ot BETWEEN '");
        return stringBuilder;
    }

    private static StringBuilder ListIndividualQuery() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT bi.date_ot 'Дата', o.name 'Собственник', concat(b.seria,' № ', LPAD(b.number,7,0)) 'Серия, номер', tar.summa_oplaty 'Сумма оплаты' FROM `to`.blanc_ts_info bi left join `to`.blanc b on bi.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=bi.id_ts_info left join `to`.reg_number r on r.id_reg_number=i.id_reg_number left join `to`.s_ts_model m on m.id_ts_model=i.id_ts_marca left join `to`.s_ts_categ c on c.id_ts_categ=i.id_ts_categ left join `to`.sd_tarifs_ts_info tar on tar.id_ts_info=bi.id_ts_info and tar.id_blanc=bi.id_blanc left join `to`.owner_info o on o.id_owner=i.id_owner_sobs left join `to`.s_conclusion con on con.id_conclusion=bi.id_conclusion WHERE bi.date_ot BETWEEN '");
        return stringBuilder;
    }

    private static StringBuilder ForSlutskQuery() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT bi.date_ot 'Дата', concat(b.seria,' ',LPAD(b.number,7,0)) 'Серия, номер', r.name 'Рег.знак', m.name 'Марка/модель', LEFT(c.name,2) 'Категория', tar.summa_oplaty 'Сумма оплаты', o.name 'Собственник', if(bi.id_blanc_repeat is null,\"Первичная\",\"Повторная\") 'Проверка', CASE bi.id_conclusion WHEN 1 THEN \"С\" WHEN 2 THEN \"X\" WHEN 3 THEN \"С с З\" END 'Результат' FROM `to`.blanc_ts_info bi left join `to`.blanc b on bi.id_blanc=b.id_blanc left join `to`.ts_info i on i.id_ts_info=bi.id_ts_info left join `to`.reg_number r on r.id_reg_number=i.id_reg_number left join `to`.s_ts_model m on m.id_ts_model=i.id_ts_marca left join `to`.s_ts_categ c on c.id_ts_categ=i.id_ts_categ left join `to`.sd_tarifs_ts_info tar on tar.id_ts_info=bi.id_ts_info and tar.id_blanc=bi.id_blanc left join `to`.owner_info o on o.id_owner=i.id_owner_sobs left join `to`.s_conclusion con on con.id_conclusion=bi.id_conclusion WHERE bi.date_ot BETWEEN '");
        return stringBuilder;
    }

    private static StringBuilder ActiveListQuery() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT r.`name` AS 'Рег. знак', LEFT(c.`name`, 2) AS 'Категория', tm.`name` AS 'Тип двигателя', m.`name` AS 'Марка/Модель', o1.`name` AS 'Владелец', o.`name` AS 'Собственник', o3.`name` AS 'Заказчик', ROUND((tar.summa_oplaty / 1.2), 2) AS `Услуги без НДС`, ROUND((tar.summa_oplaty - (tar.summa_oplaty / 1.2)), 2) AS `НДС 20%`, tar.summa_oplaty AS `Всего с НДС` FROM ts_info i LEFT JOIN owner_info o1 ON i.id_owner_vlad = o1.id_owner LEFT JOIN owner_info o ON i.id_owner_sobs = o.id_owner LEFT JOIN owner_info o3 ON i.id_owner_zakazch = o3.id_owner LEFT JOIN s_ts_model m ON m.id_ts_model = i.id_ts_marca LEFT JOIN s_ts_categ c ON c.id_ts_categ = i.id_ts_categ LEFT JOIN sd_tarifs_ts_info tar ON tar.id_ts_info = i.id_ts_info LEFT JOIN reg_number r ON i.id_reg_number = r.id_reg_number LEFT JOIN s_ts_type_motor tm ON i.id_ts_type_motor = tm.id_ts_type_motor WHERE i.for_gto = 1 AND i.is_active = 1 AND tar.id_blanc IS NULL ");
        return stringBuilder;
    }

    private static StringBuilder ForBTOQuery(String fromDate, String beforeDate) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT COUNT(`bti`.`id_blanc_ts_info`) AS \"total\", (SELECT COUNT(`bti`.`id_blanc_ts_info`) FROM `blanc_ts_info` AS `bti` LEFT JOIN `blanc` AS `b` ON `bti`.`id_blanc` = `b`.`id_blanc` WHERE `b`.`id_blanc_status` = 2 AND `b`.`id_blanc_type` = 1 AND `bti`.`id_conclusion` = 1 AND `bti`.`date_ot` BETWEEN '");
        stringBuilder.append(fromDate);
        stringBuilder.append("' AND '");
        stringBuilder.append(beforeDate);
        stringBuilder.append("') AS \"good\", (SELECT COUNT(`bti`.`id_blanc_ts_info`) FROM `blanc_ts_info` AS `bti` LEFT JOIN `blanc` AS `b` ON `bti`.`id_blanc` = `b`.`id_blanc` WHERE `b`.`id_blanc_status` = 2 AND `b`.`id_blanc_type` = 1 AND `bti`.`id_conclusion` = 3 AND `bti`.`date_ot` BETWEEN '");
        stringBuilder.append(fromDate);
        stringBuilder.append("' AND '");
        stringBuilder.append(beforeDate);
        stringBuilder.append("') AS \"good2\", (SELECT COUNT(`bti`.`id_blanc_ts_info`) FROM `blanc_ts_info` AS `bti` LEFT JOIN `blanc` AS `b` ON `bti`.`id_blanc` = `b`.`id_blanc` WHERE `b`.`id_blanc_status` = 2 AND `b`.`id_blanc_type` = 1 AND `bti`.`id_conclusion` = 2 AND `bti`.`date_ot` BETWEEN '");
        stringBuilder.append(fromDate);
        stringBuilder.append("' AND '");
        stringBuilder.append(beforeDate);
        stringBuilder.append("') AS \"bad\", SUM(`stti`.`summa_oplaty`) AS \"summa\" FROM `blanc_ts_info` AS `bti` LEFT JOIN `blanc` AS `b` ON `bti`.`id_blanc` = `b`.`id_blanc` LEFT JOIN `sd_tarifs_ts_info` AS `stti` ON `bti`.`id_blanc` = `stti`.`id_blanc` AND `bti`.`id_ts_info` = `stti`.`id_ts_info` WHERE `bti`.`date_ot` BETWEEN '");
        stringBuilder.append(fromDate);
        stringBuilder.append("' AND '");
        stringBuilder.append(beforeDate);
        stringBuilder.append("' AND `bti`.`id_conclusion` IS NOT NULL AND `b`.`id_blanc_status` = 2 AND `b`.`id_blanc_type` = 1;");
        return stringBuilder;
    }

    private static StringBuilder OrderByTariffQuery() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("SELECT `sttt`.`name`, COUNT(`tit`.`id_ts_info_tarifs`) AS \"count\", SUM(`tit`.`summa`) AS 'sum' FROM `sd_tarifs_ts_type` AS `sttt` LEFT JOIN `ts_info_tarifs` AS `tit` ON `sttt`.`id_tarifs_ts_type` = `tit`.`id_tarifs_ts_type` LEFT JOIN `ts_info` AS `ti` ON `tit`.`id_ts_info` = `ti`.`id_ts_info` LEFT JOIN `blanc_ts_info` AS `bi` ON `ti`.`id_ts_info` = `bi`.`id_ts_info` LEFT JOIN `blanc` AS `b` ON `bi`.`id_blanc` = `b`.`id_blanc` LEFT JOIN `owner_info` AS `o` ON `ti`.`id_owner_sobs` = `o`.`id_owner` LEFT JOIN `sd_tarifs_ts_info` AS `tar` ON `ti`.`id_ts_info` = `tar`.`id_ts_info` AND `bi`.`id_blanc` = `tar`.`id_blanc` WHERE `bi`.`id_blanc` IS NOT NULL AND `bi`.`id_ts_info` IS NOT NULL AND `b`.`id_blanc_type` = 1 AND `b`.`id_blanc_status` = 2 AND `bi`.`date_ot` BETWEEN '");
        return stringBuilder;
    }

    private App() {
    }
}
