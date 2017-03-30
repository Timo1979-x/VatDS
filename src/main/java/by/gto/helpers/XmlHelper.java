package by.gto.helpers;

/**
 * Created by ltv on 06.12.2016.
 */
public class XmlHelper {
    public static String replaceXmlSymbols(String in) {
        if (in == null) {
            return null;
        } else {
            return in
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("&", "&amp;");
        }
    }
}
