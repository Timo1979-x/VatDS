package by.gto.helpers;

import by.avest.crypto.pkcs11.provider.ProviderFactory;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyPair;
import java.security.Provider;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;

/**
 * Created by timo on 13.01.2017.
 */
public class AvestHelpers {
    private static boolean avestCSPPresent = false;
    private static final Logger log = Logger.getLogger(AvestHelpers.class);
    private final static String[] REG_PATHS_AVEST_JCP_LOCATION = new String[]{
            "SOFTWARE\\WOW6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\AvJCEProv_is1",
            "SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\AvJCEProv_is1"
    };

    public static boolean initAvest() {
        boolean is64bit;
        if (System.getProperty("os.name").contains("Windows")) {
            is64bit = (System.getenv("ProgramFiles(x86)") != null);
        } else {
            is64bit = (System.getProperty("os.arch").indexOf("64") != -1);
        }
        System.setProperty("by.avest.loader.shared", "true");
        //System.setProperty("java.util.logging.config.file", "OFF"); //?????

        String f = getAvestJCPPath();
        if (StringUtils.isEmpty(f)) {
            return false;
        }
//            String f = new File(AvestHelpers.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParent();

        String dllPath = null;
//            String dllPath = f + "\\win" + (is64bit ? "64" : "32");
//            String dllPath = f + "\\win" + (!is64bit ? "64" : "32");
//            System.setProperty("java.library.path", dllPath);
        avestCSPPresent = false;
        for (String p : is64bit ? (new String[]{"64", "32"}) : (new String[]{"32", "64"})) {
            dllPath = f + "\\win" + p;
            System.setProperty("java.library.path", dllPath);
            try {
                ProviderFactory.addAvUniversalProvider();
                avestCSPPresent = true;
                break;
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        log.info("java.library.path" + dllPath);
        return avestCSPPresent;
    }


    public static String getAvestJCPPath() {
        return PathHelpers.getBaseLocation();
//        try {
//            URI uri = AvestHelpers.class.getProtectionDomain().getCodeSource().getLocation().toURI();
//            return new File(uri).getAbsolutePath();
////            return new File(uri).getParent();
//        } catch (URISyntaxException e) {
//            log.error("cannot find path to our executable", e);
//            return "";
//        }
//        for (String location : REG_PATHS_AVEST_JCP_LOCATION) {
//            try {
//                String path = Advapi32Util.registryGetStringValue(
//                        WinReg.HKEY_LOCAL_MACHINE, location, "InstallLocation");
//                path = StringUtils.stripEnd(path, "\\/");
//                log.info("AVEST JCP PATH: " + path);
//                return path;
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//            }
//        }
//        return null;
    }

    /**
     * проверяет, что установлены ключи в хранилище Avest, что они валидны и обновлены СОС
     *
     * @return null, если всё в порядке, иначе - сообщение об ошибке
     */
//    public static String checkCertificates() {
//        if (!avestCSPPresent) {
//            return "Не обнаружен криптопровайдер Avest";
//        }
//        KeyStore ksAvest = null;
//        try {
//
//            // add BC provider^
//            Security.addProvider(new BouncyCastleProvider());
//
//            final AvestProvider avestProvider = ProviderFactory.addAvUniversalProvider();
//
//
//            KeyStore ksWinPersonal = KeyStore.getInstance("Windows-ROOT");
//            ksWinPersonal.load(null, null);
//            Enumeration en = ksWinPersonal.aliases();
//            while (en.hasMoreElements()) {
//                String aliasKey = (String) en.nextElement();
//                Certificate c = ksWinPersonal.getCertificate(aliasKey);
//                if (c instanceof X509Certificate) {
//                    System.out.println("---> alias : " + aliasKey);
//                    X509Certificate x = ((X509Certificate) c);
//                    System.out.println("DN " + x.getSubjectDN());
//
//                    System.out.println("IssuerDN " + x.getIssuerDN());
//                    System.out.println("IssuerX500Principal " + x.getIssuerX500Principal());
//                }
//                if (ksWinPersonal.isKeyEntry(aliasKey)) {
//                    Certificate[] chain = ksWinPersonal.getCertificateChain(aliasKey);
//                    System.out.println("---> chain length: " + chain.length);
//                    for (Certificate cert : chain) {
//                        System.out.println(cert);
//                    }
//                }
//            }
//
//            ksAvest = KeyStore.getInstance("AvPersonal", avestProvider);
//            ksAvest.load((InputStream) null, (char[]) null);
//            Enumeration<String> aliases = ksAvest.aliases();
//            while (aliases.hasMoreElements()) {
//                final String alias = aliases.nextElement();
//                System.out.println(alias);
//                Certificate cert = ksAvest.getCertificate(alias);
//                if (cert instanceof X509Certificate) {
//                    X509Certificate x509Cert = (X509Certificate) cert;
//                    try {
//                        x509Cert.checkValidity();
//                        final Certificate[] certificateChain = ksAvest.getCertificateChain(alias);
//                        HashSet<X509Certificate> set = new HashSet<>();
//                        for (int i = 1; i < certificateChain.length; i++) {
//                            set.add((X509Certificate) certificateChain[i]);
//                        }
//                        final PKIXCertPathBuilderResult pkixCertPathBuilderResult = CertificateVerifier.verifyCertificate(x509Cert, set);
//                        System.out.println(pkixCertPathBuilderResult);
//                        System.out.println("cert " + x509Cert + " valid");
//                    } catch (CertificateExpiredException | CertificateNotYetValidException e) {
//                        System.err.println(e);
//                    }
//                }
//                System.out.println(getCertificateInfo(cert));
////                CertPathBuilder cpBuilder = null;
////                for (String algorythm : new String[]{"AvTLSMasterSecret",
////                        "AvTLSKeyMaterial",
////                        "BhfWithBds",
////                        "BhfWithCompoundBds",
////                        "AvBhfWithAvBds",
////                        "AvBhfWithAvCompoundBds",
////                        "AvBhfWithAvBdsHash",
////                        "AvBhfWithAvCompoundBdsHash",
////                        "BelTWithBdsHash",
////                        "AvBelTWithAvBdsHash",
////                        "BelTWithBign",
////                        "AvBhfWithBign",
////                        "Bhf",
////                        "AvBhf",
////                        "BelT",
////                        "AvBelT",
////                        "Bds",
////                        "AvBds",
////                        "AvBdh",
////                        "BdhEphemer",
////                        "BdsHash",
////                        "AvBdsHash",
////                        "CompoundBds",
////                        "AvCompoundBds",
////                        "CompoundBdsHash",
////                        "AvCompoundBdsHash",
////                        "Bign",
////                        "GOST_28147_89CipherParams",
////                        "AvGOST_28147_89CipherParams",
////                        "BelTCipherParams",
////                        "BDHWrap",
////                        "AvBDHWrap",
////                        "BignWrap",
////                        "GOST_28147_89/ECB",
////                        "GOST_28147_89/CTR",
////                        "GOST_28147_89/CFB",
////                        "BelT/ECB",
////                        "BelT/CTR",
////                        "BelT/CFB",
////                        "BelT/CBC",
////                        "GOST_28147_89",
////                        "BelT",
////                        "HmacBelT",
////                        "GOST_28147_89",
////                        "SignatureDual/XXX",
////                        "DigestDual/XXX",
////                        "BelT",
////                        "AvPersonal",
////                        "AvRoot",
////                        "AvCA",
////                        "BelPrd",
////                        "GOST_28147_89",
////                        "BelT",
////                        "Bdh",
////                        "GOST_28147_89",
////                        "BelT",
////                        "Generic",
////                        "Bdh/NoAuth",
////                        "AvBDHWrap",
////                        "BDHWrap",
////                        "BignWrap",
////                        "TLSBelTMac/CTR"}) {
////                    try {
////                        cpBuilder = CertPathBuilder.getInstance(algorythm, avestProvider);
////                        System.out.println(algorythm + " is OK");
////                        AvPKIXParameters params = new AvPKIXParameters(ksAvest);
////                        params.setRevocationEnabled(true);
////                        final CertPathBuilderResult cpbResult = cpBuilder.build(params);
////                        final CertPath certPath = cpbResult.getCertPath();
////                        final List<? extends Certificate> certificates = certPath.getCertificates();
////                        final String cpType = certPath.getType();
////                    } catch (Exception e) {
////                    }
////                }
//            }
//        } catch (Exception e) {
//            String msg = ExceptionHelpers.extractMessage(e);
//            log.error(msg);
//            return "ошибка: " + msg;
//        }
//        return null;
//    }

    public static String bytesAsHexString(byte[] input) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (byte b : input) {
            sb.append(String.format("%02x", b));
            if (++i % 16 == 0) {
                sb.append('\n');
            } else {
                sb.append(' ');
            }
        }
        return sb.append('\n').toString();
    }

    public static String getKeyInfo(Key key) {
        StringBuilder sb = new StringBuilder();
        sb.append("algorythm: ").append(key.getAlgorithm())
                .append(" format: ").append(key.getFormat())
                .append(" class: ").append(key.getClass().getCanonicalName())
                .append("\nencoded key: ").append(bytesAsHexString(key.getEncoded()));
        return sb.toString();
    }

    public static String getCertificateInfo(Certificate cert) throws CertificateEncodingException {

        StringBuilder sb = new StringBuilder();
        sb.append("class: ").append(cert.getClass().getCanonicalName())
                .append("\n type: ").append(cert.getType());
        sb.append("\npub key: ").append(getKeyInfo(cert.getPublicKey()))
                .append("\nencoded cert: ").append(bytesAsHexString(cert.getEncoded()));
        return sb.toString();
    }

    public static String getCipherInfo(Cipher cipher, boolean showProperties, boolean showServices) {
        StringBuilder sb = new StringBuilder();
        sb.append("cipher class: ").append(cipher.getClass().getCanonicalName())
                .append(" algorithm: ").append(cipher.getAlgorithm())
                .append(" block size: ").append(cipher.getBlockSize())
                .append(" ExemptionMechanism: ").append(cipher.getExemptionMechanism())
                .append(" provider info: ").append(cipher.getProvider().getInfo())
                .append(" provider name: ").append(cipher.getProvider().getName())
                .append(" provider version: ").append(cipher.getProvider().getVersion())
                .append('\n');
        int counter;
        if (showProperties) {
            sb.append("Cipher provider properties:\n");
            for (Object propName : cipher.getProvider().stringPropertyNames()) {
                sb.append("property ").append(propName).append(" -> ").append(cipher.getProvider().get(propName)).append('\n');
            }
        }
        if (showServices) {
            counter = 0;
            sb.append("Cipher provider services:\n");
            for (Provider.Service service : cipher.getProvider().getServices()) {
                sb.append("service").append(counter++).append(service.toString()).append('\n');
            }
        }
        return sb.toString();
    }

    public static String getKeyPairInfo(KeyPair kp) {
        StringBuilder sb = new StringBuilder();
        sb.append("KeyPair class: ").append(kp.getClass().getCanonicalName())
                .append("\npublic key: ").append(getKeyInfo(kp.getPublic()))
                .append("\nprivate key: ").append(getKeyInfo(kp.getPrivate()))
                .append('\n');

        return sb.toString();
    }

    public static boolean isAvestCSPPresent() {
        return avestCSPPresent;
    }
}
