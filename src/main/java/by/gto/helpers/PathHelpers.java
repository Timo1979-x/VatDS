package by.gto.helpers;

import org.apache.log4j.Logger;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

public class PathHelpers {
    private static String dataDirectory = "d:\\";
    private static final Logger log = Logger.getLogger(PathHelpers.class);

    static {
        dataDirectory = System.getenv("APPDATA") + "\\Beltehosmotr\\vatDS";
    }

    /**
     * Возвращает путь к корневой папке программы (той, где лежит запускаемый *.exe, *jar
     * либо корневой путь для классов в случае, когда программа запускается не из exe или jar)
     * @return путь к корневой папке программы
     */
    public static String getBaseLocation() {
        File baseLocation = null;
        try {
            baseLocation = new File(PathHelpers.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            if (baseLocation.isFile()) {
                return baseLocation.getParent();
            }
            return Paths.get(baseLocation.getParent(),"resources").toString();
//            return baseLocation.getAbsolutePath();
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
            return "";
        }
    }

    /**
     * Возвращает путь к папке с данными (там будет база данных, логи и т.д.)
     * @return путь к папке с данными. Где-то в дебрях Application Data
     */
    public static String getDataDirectory() {
        return dataDirectory;
    }
}
