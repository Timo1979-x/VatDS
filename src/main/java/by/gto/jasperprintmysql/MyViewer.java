package by.gto.jasperprintmysql;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JRSaveContributor;
import net.sf.jasperreports.view.JasperViewer;
import net.sf.jasperreports.view.save.JRCsvSaveContributor;
import net.sf.jasperreports.view.save.JRDocxSaveContributor;
import net.sf.jasperreports.view.save.JREmbeddedImagesXmlSaveContributor;
import net.sf.jasperreports.view.save.JRHtmlSaveContributor;
import net.sf.jasperreports.view.save.JRMultipleSheetsXlsSaveContributor;
import net.sf.jasperreports.view.save.JROdtSaveContributor;
import net.sf.jasperreports.view.save.JRPdfSaveContributor;
import net.sf.jasperreports.view.save.JRPrintSaveContributor;
import net.sf.jasperreports.view.save.JRRtfSaveContributor;
import net.sf.jasperreports.view.save.JRSingleSheetXlsSaveContributor;
import net.sf.jasperreports.view.save.JRXmlSaveContributor;

/**
 *
 * @author Aleks
 */
public class MyViewer extends JasperViewer {

    /**
     *
     * @param jasperPrint JasperPrint
     * @param isExitOnClose Выходить из приложения при закрытии
     * @param saveContr  В каком формате сохранять (напр.: pdf, rtf, xml )pdf - JRPdfSaveContributor multipleXLS -
     * JRMultipleSheetsXlsSaveContributor rtf - JRRtfSaveContributor odt -
     * JROdtSaveContributor docx - new JRDocxSaveContributor html -
     * JRHtmlSaveContributor singleXLS - JRSingleSheetXlsSaveContributor csv -
     * JRCsvSaveContributor xml - JRXmlSaveContributor embImgXml -
     * JREmbeddedImagesXmlSaveContributor print - JRPrintSaveContributor
     */
    public MyViewer(JasperPrint jasperPrint, boolean isExitOnClose, String saveContr) {
        super(jasperPrint, isExitOnClose);
        Locale locale = viewer.getLocale();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("net/sf/jasperreports/view/viewer", locale);

        StringTokenizer st = new StringTokenizer(saveContr, ",");
        List<String> saveList = new ArrayList<>();
        while (st.hasMoreTokens()) {
            saveList.add(st.nextToken().trim());
        }

        JRSaveContributor[] save = new JRSaveContributor[saveList.size()];
        for (int i = 0; i < saveList.size(); i++) {
            switch (saveList.get(i)) {
                case "pdf":
                    save[i] = new JRPdfSaveContributor(locale, resourceBundle);
                    break;
                case "multipleXLS":
                    save[i] = new JRMultipleSheetsXlsSaveContributor(locale, resourceBundle);
                    break;
                case "rtf":
                    save[i] = new JRRtfSaveContributor(locale, resourceBundle);
                    break;
                case "odt":
                    save[i] = new JROdtSaveContributor(locale, resourceBundle);
                    break;
                case "docx":
                    save[i] = new JRDocxSaveContributor(locale, resourceBundle);
                    break;
                case "html":
                    save[i] = new JRHtmlSaveContributor(locale, resourceBundle);
                    break;
                case "singleXLS":
                    save[i] = new JRSingleSheetXlsSaveContributor(locale, resourceBundle);
                    break;
                case "csv":
                    save[i] = new JRCsvSaveContributor(locale, resourceBundle);
                    break;
                case "xml":
                    save[i] = new JRXmlSaveContributor(locale, resourceBundle);
                    break;
                case "embImgXml":
                    save[i] = new JREmbeddedImagesXmlSaveContributor(locale, resourceBundle);
                    break;
                case "print":
                    save[i] = new JRPrintSaveContributor(locale, resourceBundle);
                    break;
            }
        }
        /**
         * JRPrintSaveContributor JRPdfSaveContributor JRRtfSaveContributor
         * JROdtSaveContributor JRDocxSaveContributor JRHtmlSaveContributor
         * JRSingleSheetXlsSaveContributor JRMultipleSheetsXlsSaveContributor
         * JRCsvSaveContributor JRXmlSaveContributor
         * JREmbeddedImagesXmlSaveContributor
         */
        viewer.setSaveContributors(save);
    }
}
