package by.gto.helpers;

import by.gto.model.AgreementData;
import by.gto.model.BranchInfo;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.format.CellFormat;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ExcelLoader {

    private static SimpleDateFormat df1 = new SimpleDateFormat("M/d/yy");
    private static SimpleDateFormat[] dateFormats = new SimpleDateFormat[]{
            new SimpleDateFormat("dd.MM.yyyy"),
            new SimpleDateFormat("yyyy-MM-dd")
    };

    private static final Logger log = Logger.getLogger(ExcelLoader.class);
    private static DataFormatter excelDataFormatter = new DataFormatter(Locale.US);

//    public synchronized static List<BranchInfo> loadBranchInfos(File file, StringBuilder errorList) {
//        Workbook wb = null;
//        boolean wasErrors = false;
//        try {
//            wb = WorkbookFactory.create(file);
//        } catch (InvalidFormatException | IOException e) {
//            errorList.append("Ошибка открытия файла Excel ").append(e.getMessage());
//            return null;
//        }
//
//        try {
//            List<BranchInfo> result = new ArrayList<>();
//            final Sheet firstSheet = wb.getSheetAt(0);
//            final int lastRowNum = firstSheet.getLastRowNum();
//            for (int rowNum = 1; rowNum <= lastRowNum; rowNum++) {
//                try {
//                    Row row = firstSheet.getRow(rowNum);
//                    int unp = getExcelInteger(row, 0);
//                    if (unp < 100000000) {
//                        break;
//                    }
//                    result.add(new BranchInfo(unp, getExcelString(row, 3), getExcelInteger(row, 1)));
//                } catch (Exception e) {
//                    log.error(e.getMessage(), e);
//                    errorList.append("\n").append(e.getMessage()).append("\n").append(e);
//                    wasErrors = true;
//                }
//            }
//            return wasErrors ? null : result;
//        } finally {
//            try {
//                wb.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    public synchronized static List<AgreementData> loadRegistryFile(File file, StringBuilder errorList) {
        Workbook wb = null;
        boolean wasErrors = false;
        try {
            wb = WorkbookFactory.create(file);
        } catch (InvalidFormatException | IOException e) {
            errorList.append("ошибка открытия файла Excel").append(e.getMessage());
            return null;
        }
        int rowNum = 0;
        try {
            List<AgreementData> result = new ArrayList<>();
            final Sheet firstSheet = wb.getSheetAt(2);
            final int lastRowNum = firstSheet.getLastRowNum();
            for (rowNum = 1; rowNum <= lastRowNum; rowNum++) {
                try {
                    Row row = firstSheet.getRow(rowNum);
                    if(row==null) {
                        break;
                    }
                    Integer customerUnp = getExcelInteger(row, 6);
                    if (customerUnp == null || customerUnp < 10) {
                        continue;
                    }
                    if (customerUnp < 100000000) {
                        errorList.append("\n").append("Неправильный УНП: ").append(customerUnp);
                    }
                    String agrNumber = getExcelString(row, 2);
                    Date agrDate = getExcelDate2(row, 3);

                    if (agrDate != null) {
                        result.add(
                                new AgreementData(customerUnp, agrNumber, agrDate));
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                    errorList.append("\n").append(e.getMessage()).append("\n").append(e);
                    wasErrors = true;
                }
            }
            return wasErrors ? null : result;
        } finally {
            try {
                wb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static BigDecimal getExcelCurrency(Row row, int colNum) {
        final org.apache.poi.ss.usermodel.Cell cell = row.getCell(colNum);
        switch (cell.getCellTypeEnum()) {
            case NUMERIC:
                return new BigDecimal(cell.getNumericCellValue());
            case STRING:
                return new BigDecimal(cell.getStringCellValue());
            default:
                return BigDecimal.ZERO;
        }
    }

    private static Integer getExcelInteger(Row row, int colNum) {
        final org.apache.poi.ss.usermodel.Cell cell = row.getCell(colNum);
        if (cell == null) {
            return null;
        }
        switch (cell.getCellTypeEnum()) {
            case NUMERIC:
                return (int) (cell.getNumericCellValue());
            case STRING:
                String stringCellValue = cell.getStringCellValue();
                if (stringCellValue == null) {
                    return null;
                }
                return Double.valueOf(StringUtils.trim(stringCellValue)).intValue();
            default:
                return null;
        }
    }

//    private static Date getExcelDate(Row row, int colNum) {
//        final org.apache.poi.ss.usermodel.Cell cell = row.getCell(colNum);
//        switch (cell.getCellTypeEnum()) {
//            case NUMERIC:
//                return cell.getDateCellValue();
//            case STRING:
//                String s = cell.getStringCellValue();
//                Date result = null;
//                for (SimpleDateFormat df : dateFormats) {
//                    try {
//                        result = df.parse(s);
//                        break;
//                    } catch (ParseException e) {
//                    }
//                }
//                return result;
//            default:
//                return null;
//        }
//    }

    private static String getExcelString(Row row, int colNum) {
        final org.apache.poi.ss.usermodel.Cell cell = row.getCell(colNum);
        if (null == cell) {
            return "";
        }
        String result;
        switch (cell.getCellTypeEnum()) {
            case STRING:
                result = cell.getStringCellValue();
                break;
            default:
                CellStyle style = cell.getCellStyle();
                CellFormat cf = CellFormat.getInstance(
                        style.getDataFormatString());
                result = cf.apply(cell).text;
                break;
        }
        return result;
    }

    private static BigDecimal getExcelCurrency2(Row row, int colNum) {
        final Cell cell = row.getCell(colNum);
        if (cell == null) {
            return BigDecimal.ZERO;
        }
        final String val = excelDataFormatter.formatCellValue(cell);

        try {
            try {
                return new BigDecimal(val);
            } catch (NumberFormatException ex) {
            }
            return new BigDecimal(val.replace(',', '.'));
        } catch (Exception e) {

            return BigDecimal.ZERO;
        }
    }

    private static Date getExcelDate2(Row row, int colNum) {
        final org.apache.poi.ss.usermodel.Cell cell = row.getCell(colNum);
        if (cell == null) {
            return null;
        }
        final String formattedCellValue = excelDataFormatter.formatCellValue(cell);
        switch (cell.getCellTypeEnum()) {
            case NUMERIC:
                try {
                    CellStyle style = cell.getCellStyle();
                    final String dataFormatString = style.getDataFormatString().split(";")[0].replace('m', 'M');
                    return new SimpleDateFormat(dataFormatString).parse(formattedCellValue);
                } catch (Exception e) {
                    return null;
                }
            case STRING:
                for (SimpleDateFormat sdf : dateFormats) {
                    try {
                        return sdf.parse(formattedCellValue);
                    } catch (Exception e) {
                    }

                }
                return null;
        }
        return null;
    }

    private static String getExcelString2(Row row, int colNum) {
        final org.apache.poi.ss.usermodel.Cell cell = row.getCell(colNum);

        CellStyle style = cell.getCellStyle();
        CellFormat cf = CellFormat.getInstance(
                style.getDataFormatString());
        return cf.apply(cell).text;
    }



}
