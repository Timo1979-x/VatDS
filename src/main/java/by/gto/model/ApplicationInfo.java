package by.gto.model;

import by.gto.library.App;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

public class ApplicationInfo {
    private static final Logger logger = Logger.getLogger(ApplicationInfo.class);
    private static ApplicationInfo instance = null;
    private String name = "???";
    private String descriptiveName = "???";
    private String version = "0.0.0";
    private String vendor = "???";
    private String buildDate = "???";

    public static ApplicationInfo getInstance() {
        if (null == instance) {
            instance = loadFromManifest();
        }
        return instance;
    }

    private ApplicationInfo() { // I'm singleton!
    }

    public String getName() {
        return name;
    }

    public String getDescriptiveName() {
        return descriptiveName;
    }

    public String getVersion() {
        return version;
    }

    public String getVendor() {
        return vendor;
    }

    public String getBuildDate() {
        return buildDate;
    }

    /**
     * не работает при запуске из-под IDE, т.к. в этом случае НЕ СОЗДАЕТСЯ jar.
     * @return
     */
    private static ApplicationInfo loadFromManifest() {
        ApplicationInfo r = new ApplicationInfo();

//        try {
//            ApplicationInfo.class.getProtectionDomain().getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF");
//            File cp = new File(ApplicationInfo.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }

        try (InputStream s = ApplicationInfo.class.getProtectionDomain().getClassLoader().getResourceAsStream("META-INF/MANIFEST.MF");
//                InputStream s = ApplicationInfo.class.getResourceAsStream("META-INF/MANIFEST.MF");
             InputStreamReader isr = new InputStreamReader(s, "UTF-8");
             BufferedReader br = new BufferedReader(isr)) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("Implementation-Version:")) {
                    r.version = line.split(":\\s+")[1];
                } else if (line.startsWith("Implementation-Title:")) {
                    r.descriptiveName = line.split(":\\s+")[1];
                } else if (line.startsWith("Name:")) {
                    r.name = line.split(":\\s+")[1];
                } else if (line.startsWith("Implementation-Vendor:")) {
                    r.vendor = line.split(":\\s+")[1];
                } else if (line.startsWith("build-time:")) {
                    r.buildDate = line.split(":\\s+")[1];
                }
            }
        } catch (Exception ignored) {
            logger.error(ignored, ignored);
        }
        return r;
    }
}
