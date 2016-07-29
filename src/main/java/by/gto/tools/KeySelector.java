package by.gto.tools;

import by.avest.edoc.client.PersonalKeyManager2;
import by.gto.btoreport.gui.MainController;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Tim on 23.07.2016.
 */
public class KeySelector extends PersonalKeyManager2 {
    private Map<String, String> passwords = new HashMap<>();

    public KeySelector(KeyStore ks) {
        super(ks);
    }

    public KeySelector() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        super(getDefaultKS());
    }

    private static KeyStore getDefaultKS() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
        KeyStore ks = KeyStore.getInstance("AvPersonal");
        ks.load((InputStream) null, (char[]) null);
        return ks;
    }

    public char[] promptPassword(String alias) throws IOException {
        final String pass = System.getProperty("by.gto.btoreport.avest.password");
        if(pass != null) {
            return pass.toCharArray();
        }
        if(passwords.containsKey(alias)) {
            return passwords.get(alias).toCharArray();
        }
        if(chooseAlias(new String[] {alias}) != null) {
            return passwords.get(alias).toCharArray();
        }
        return new char[0];
    }

    public String chooseAlias(String[] aliases) throws IOException {
        for (String alias : aliases) {
            final PrivateKey pk = this.getPrivateKey(alias);
            System.out.println(pk);
        }
        passwords.clear();
        Object[] result = MainController.chooseFromList("Выберите ключ", aliases);
        int idx = (int) result[0];
        if(idx >= 0) {
            String alias = aliases[idx];
            passwords.put(alias, (String)result[1]);
            return alias;
        }
        return "";
    }



}
