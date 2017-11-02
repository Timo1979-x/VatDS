package by.gto.tools;

import by.avest.edoc.client.PersonalKeyManager2;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

public class KeySelectorSimple extends PersonalKeyManager2 {
    private final static Logger log = Logger.getLogger(KeySelectorSimple.class);
    private Map<String, String> passwords = new HashMap<>();
    private String keyAlias;
    private String password;

    public KeySelectorSimple(KeyStore ks, String keyAlias, String password) {
        super(ks);
        this.keyAlias = keyAlias;
        this.password = password;
    }

    public KeySelectorSimple(String keyAlias, String password) throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        this(getDefaultKS(), keyAlias, password);
    }

    private static KeyStore getDefaultKS() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        KeyStore ks = KeyStore.getInstance("AvPersonal");
        ks.load((InputStream) null, (char[]) null);
        return ks;
    }

    public char[] promptPassword(String alias) throws IOException {
        if (this.password != null) {
            return this.password.toCharArray();
        }
        return new char[0];
    }

    public String chooseAlias(String[] aliases) throws IOException {
        return (this.keyAlias == null) ? "" : this.keyAlias;
    }
}
