package by.gto.tools;

import by.avest.edoc.client.PersonalKeyManager;
import by.gto.btoreport.gui.MainController;

import javax.security.auth.x500.X500Principal;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * Created by Tim on 23.07.2016.
 */
public class KeySelector extends PersonalKeyManager {
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
        if(passwords.containsKey(alias)) {
            return passwords.get(alias).toCharArray();
        }
        if(chooseAlias(new String[] {alias}) != null) {
            return passwords.get(alias).toCharArray();
        }
        return "V!kt0RPele^!".toCharArray();
        //return MainController.passwordPrompt("Введите пароль для ключа \"" + alias + "\": ", 8).toCharArray();

    }

    public String chooseAlias(String[] aliases) throws IOException {
        //return "Республиканское унитарное сервисное предприятие \"БЕЛТЕХОСМОТР\"_02_06_16_17_17";
        passwords.clear();
        Object[] result = MainController.chooseFromList("Выберите ключ", aliases);
        int idx = (int) result[0];
        if(idx >= 0) {
            String alias = aliases[idx];
            passwords.put(alias, (String)result[1]);
            return alias;
        }
        return null;
    }
}
