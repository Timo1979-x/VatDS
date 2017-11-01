package by.gto.helpers;

import by.gto.tools.ConnectionMySql;
import com.mysql.jdbc.Connection;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ExportHelpers {

    private static String queryDCfor1c = "SELECT\n" +
            "  `bti`.`code_ds`,\n" +
            "  `l`.`stationnumber`,\n" +
            "  `b`.`seria`,\n" +
            "  `b`.`number`,\n" +
            "  `bti`.`date_ot`,\n" +
            "  `stti`.`summa_no_tax`,\n" +
            "  `stti`.`summa_tax`,\n" +
            "  `stti`.`summa_oplaty`,\n" +
            "  `oi`.`unp`,\n" +
            "  CASE WHEN `ti`.`id_owner_zakazch` IS NULL THEN `oiHolder`.`Name` ELSE `oi`.`Name` END AS `Name`,\n" +
            "  CASE WHEN `ti`.`id_owner_zakazch` IS NULL THEN `oiHolder`.`id_owner_type` ELSE `oi`.`id_owner_type` END AS `owner_type`\n" +
            "FROM `blanc_ts_info` AS `bti`\n" +
            "  LEFT JOIN `blanc` AS `b`\n" +
            "    ON `bti`.`id_blanc` = `b`.`id_blanc`\n" +
            "  LEFT JOIN `licences` AS `l`\n" +
            "    ON `bti`.`code_ds` = `l`.`code`\n" +
            "  LEFT JOIN `ts_info` AS `ti`\n" +
            "    ON `bti`.`id_ts_info` = `ti`.`id_ts_info`\n" +
            "  LEFT JOIN `owner_info` AS `oi`\n" +
            "    ON `ti`.`id_owner_zakazch` = `oi`.`id_owner`\n" +
            "  LEFT JOIN `owner_info` AS `oiHolder`\n" +
            "    ON `ti`.`id_owner_vlad` = `oiHolder`.`id_owner`\n" +
            "  LEFT JOIN `sd_tarifs_ts_info` AS `stti`\n" +
            "    ON `bti`.`id_blanc` = `stti`.`id_blanc`\n" +
            "    AND `ti`.`id_ts_info` = `stti`.`id_ts_info`\n" +
            "WHERE `bti`.`date_ot` BETWEEN '%s' AND '%s'\n" +
            "AND `b`.`id_blanc_status` = 2\n" +
            "AND `b`.`id_blanc_type` = 1\n" +
            "\n" +
            "ORDER BY `bti`.`date_ot`";

    private static DateTimeFormatter fullDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static DateTimeFormatter shortDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static String[] createExportFor1C(LocalDate localDateStart, LocalDate localDateStop) throws SQLException {
        String[] result = new String[2];
        StringBuilder sb = new StringBuilder(100000);
        String q = String.format(queryDCfor1c,
                localDateStart.atStartOfDay().format(fullDateFormatter),
                localDateStop.atTime(LocalTime.MAX).format(fullDateFormatter));
        try (Connection conn = ConnectionMySql.getInstance().getConn();
             PreparedStatement st = conn.prepareStatement(q)) {
//            Date d1 = Date.valueOf(localDateStart);
//            Date d2 = Date.valueOf(localDateStop);
//            st.setDate(1, d1);
//            st.setDate(2, d2);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    result[1] = rs.getString(2); //  № ДС
                    String unp = rs.getString(9);
                    unp = unp != null ? StringUtils.replace(unp, "\"", "\"\"") : "1";
                    sb.append(rs.getInt(1)).append(",")
                            .append(rs.getInt(2)).append(",\"")
                            .append(StringUtils.replace(rs.getString(3), "\"", "\"\"")).append("\",")
                            .append(rs.getInt(4)).append(",\"")
                            .append(String.format("%1$tY-%1$tm-%1$td", rs.getDate(5))).append("\",")
                            .append(rs.getBigDecimal(6).toPlainString()).append(",")
                            .append(rs.getBigDecimal(7).toPlainString()).append(",")
                            .append(rs.getBigDecimal(8).toPlainString()).append(",")
                            .append(unp).append(",\"")
                            .append(StringUtils.replace(rs.getString(10), "\"", "\"\"")).append("\",")
                            .append(rs.getInt(11)).append("\n");
                }
            }
        }
        result[0] = sb.toString();
        return result;
    }

    public static String createFileName(String dir, String prefix, LocalDate d1, LocalDate d2, String ext) {
        return Paths.get(dir,
                String.format("%s %s %s.%s", prefix, d1.atStartOfDay().format(shortDateFormatter), d2.atStartOfDay().format(shortDateFormatter), ext)
        ).toString();
    }

    public static void save2File(String filename, String content, String encoding) throws IOException {

        try (FileOutputStream fos = new FileOutputStream(filename);
             OutputStreamWriter osw = new OutputStreamWriter(fos, encoding);
             BufferedWriter bw = new BufferedWriter(osw)) {
            bw.write(content);
        }
    }
}
