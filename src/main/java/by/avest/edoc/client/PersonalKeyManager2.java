//package by.gto.tools;
package by.avest.edoc.client;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import by.avest.crypto.cert.verify.CertVerify;
import by.avest.crypto.cert.verify.CertVerifyResult;
import org.apache.log4j.Logger;
import sun.security.util.DerInputStream;
import sun.security.util.DerValue;
import sun.security.x509.X500Name;

import javax.net.ssl.X509ExtendedKeyManager;
import javax.security.auth.x500.X500Principal;
import java.io.IOException;
import java.net.Socket;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.*;

public abstract class PersonalKeyManager2 extends X509ExtendedKeyManager {
    private final static Logger log = Logger.getLogger(PersonalKeyManager2.class);
    public static final String PARAM_UNP = "UNP";
    public static final String PARAM_PUB_KEY_ID = "PUB_KEY_ID";
    public static final String PARAM_COMMON_NAME = "COMMON_NAME";
    public static final String PARAM_PASSWORD_KEY = "PASSWORD_KEY";
    protected static final String[] DEFAULT_KEY_TYPES = new String[]{"1.3.6.1.4.1.12656.1.38", "AvBds", "1.3.6.1.4.1.12656.1.35", "AvBdsHash", "1.3.6.1.4.1.12656.1.33", "AvCompoundBds", "1.3.6.1.4.1.12656.1.37", "AvCompoundBdsHash", "1.2.112.0.2.0.1176.2.2.1", "Bds", "1.2.112.0.2.0.1176.2.2.2", "BdsHash", "1.2.112.0.2.0.1176.2.2.3", "CompoundBds", "1.2.112.0.2.0.1176.2.2.4", "CompoundBdsHash", "1.2.112.0.2.0.34.101.45.2.1", "Bign"};
    private KeyStore ks;
    private CertVerify cv;
    private Map<String, by.avest.edoc.client.PersonalKeyManager2.X509Credentials> credentials;
    private String alias;
    private char[] password;
    private PrivateKey privkey;
    private X509Certificate[] certchain;
    public static final String GOSSUOK_UNP_OID = "1.2.112.1.2.1.1.1.1.2";
    public static final String RUPIIC_UNP_OID = "1.3.6.1.4.1.12656.106.101";
    public static final String CERT_SUBJ_KEY_ID_OID = "2.5.29.14";
    protected static final char[] hexArray = "0123456789ABCDEF".toCharArray();

    public PersonalKeyManager2(KeyStore ks) {
        this.ks = ks;
    }

    void setCertVerify(CertVerify cv) {
        this.cv = cv;
    }

    public void init(String init) {
        if(init == null) {
            throw new IllegalArgumentException("init");
        } else {
            StringTokenizer st = new StringTokenizer(init, ";");
            String[] aliases = null;

            while(st.hasMoreTokens()) {
                String e = st.nextToken();
                if(e.indexOf("=") < 0) {
                    throw new AvLoginException("Неверный формат параметров авторизации.");
                }

                String param = e.substring(0, e.indexOf("="));
                String value = e.substring(e.indexOf("=") + 1);

                try {
                    if(param.equalsIgnoreCase("UNP")) {
                        aliases = this.selectAliasByUNP(value);
                    } else if(param.equalsIgnoreCase("PUB_KEY_ID")) {
                        aliases = this.selectByKeyId(value);
                    } else if(param.equalsIgnoreCase("COMMON_NAME")) {
                        aliases = this.selectByCommonName(value);
                    } else if(param.equalsIgnoreCase("PASSWORD_KEY")) {
                        this.password = value.toCharArray();
                    }
                } catch (IOException var9) {
                    throw new AvLoginException(var9);
                }
            }

            if(aliases != null && aliases.length != 0) {
                if(aliases.length == 1) {
                    this.alias = aliases[0];
                } else {
                    try {
                        this.alias = this.chooseAlias(aliases);
                    } catch (IOException var8) {
                        throw new AvLoginException("При выборе контейнера с ключом клиента произошла ошибка.", var8);
                    }
                }
            } else {
                this.alias = null;
            }

        }
    }

    private String[] selectAliasByUNP(String unp) throws IOException {
        if(unp == null) {
            return null;
        } else {
            LinkedList aliases = new LinkedList();
            Iterator aliasStrings = this.getCredentials().entrySet().iterator();

            while(aliasStrings.hasNext()) {
                Map.Entry entry = (Map.Entry)aliasStrings.next();
                String alias = (String)entry.getKey();
                by.avest.edoc.client.PersonalKeyManager2.X509Credentials credentials = (by.avest.edoc.client.PersonalKeyManager2.X509Credentials)entry.getValue();
                X509Certificate[] certs = credentials.certificates;
                String certUnp = parseUNP(certs[0]);
                if(certUnp != null && certUnp.equalsIgnoreCase(unp) && this.isCertValid(certs[0])) {
                    aliases.add(alias);
                }
            }

            String[] aliasStrings1 = (String[])aliases.toArray(new String[0]);
            return aliasStrings1.length == 0?null:aliasStrings1;
        }
    }

    private String[] selectByKeyId(String keyId) throws IOException {
        if(keyId == null) {
            return null;
        } else {
            LinkedList aliases = new LinkedList();
            Iterator aliasStrings = this.getCredentials().entrySet().iterator();

            while(aliasStrings.hasNext()) {
                Map.Entry entry = (Map.Entry)aliasStrings.next();
                String alias = (String)entry.getKey();
                by.avest.edoc.client.PersonalKeyManager2.X509Credentials credentials = (by.avest.edoc.client.PersonalKeyManager2.X509Credentials)entry.getValue();
                X509Certificate[] certs = credentials.certificates;
                String certKeyId = parseKeyID(certs[0]);
                if(certKeyId != null && certKeyId.equalsIgnoreCase(keyId) && this.isCertValid(certs[0])) {
                    aliases.add(alias);
                }
            }

            String[] aliasStrings1 = (String[])aliases.toArray(new String[0]);
            return aliasStrings1.length == 0?null:aliasStrings1;
        }
    }

    private String[] selectByCommonName(String commonName) throws IOException {
        if(commonName == null) {
            return null;
        } else {
            LinkedList aliases = new LinkedList();
            Iterator aliasStrings = this.getCredentials().entrySet().iterator();

            while(aliasStrings.hasNext()) {
                Map.Entry entry = (Map.Entry)aliasStrings.next();
                String alias = (String)entry.getKey();
                by.avest.edoc.client.PersonalKeyManager2.X509Credentials credentials = (by.avest.edoc.client.PersonalKeyManager2.X509Credentials)entry.getValue();
                X509Certificate[] certs = credentials.certificates;
                X500Principal subject = certs[0].getSubjectX500Principal();
                X500Name name = new X500Name(subject.getName());
                String certCommonName = name.getCommonName();
                if(certCommonName != null && certCommonName.equalsIgnoreCase(commonName) && this.isCertValid(certs[0])) {
                    aliases.add(alias);
                }
            }

            String[] aliasStrings1 = (String[])aliases.toArray(new String[0]);
            return aliasStrings1.length == 0?null:aliasStrings1;
        }
    }

    public String[] getClientAliases(String keytype, Principal[] issuers) {
        if(keytype == null) {
            return null;
        } else {
            if(issuers == null) {
                issuers = new X500Principal[0];
            }

            if(!(issuers instanceof X500Principal[])) {
                issuers = convertPrincipals((Principal[])issuers);
            }

            String sigType = null;
            if(keytype.contains("_")) {
                int x500Issuers = keytype.indexOf("_");
                sigType = keytype.substring(x500Issuers + 1);
                keytype = keytype.substring(0, x500Issuers);
            }

            X500Principal[] var13 = (X500Principal[])((X500Principal[])issuers);
            ArrayList aliases = new ArrayList();
            Iterator aliasStrings = this.getCredentials().entrySet().iterator();

            while(true) {
                while(true) {
                    String alias;
                    by.avest.edoc.client.PersonalKeyManager2.X509Credentials credentials;
                    X509Certificate[] certs;
                    do {
                        while(true) {
                            do {
                                if(!aliasStrings.hasNext()) {
                                    String[] var14 = (String[])aliases.toArray(new String[0]);
                                    return var14.length == 0?null:var14;
                                }

                                Map.Entry entry = (Map.Entry)aliasStrings.next();
                                alias = (String)entry.getKey();
                                credentials = (by.avest.edoc.client.PersonalKeyManager2.X509Credentials)entry.getValue();
                                certs = credentials.certificates;
                            } while(!keytype.equals(certs[0].getPublicKey().getAlgorithm()));

                            if(sigType == null) {
                                break;
                            }

                            if(certs.length > 1) {
                                if(!sigType.equals(certs[1].getPublicKey().getAlgorithm())) {
                                    continue;
                                }
                            } else {
                                String certIssuers = certs[0].getSigAlgName().toUpperCase(Locale.ENGLISH);
                                String i = "WITH" + sigType.toUpperCase(Locale.ENGLISH);
                                if(!certIssuers.contains(i)) {
                                    continue;
                                }
                            }
                            break;
                        }
                    } while(!this.isCertValid(certs[0]));

                    if(((Object[])issuers).length == 0) {
                        aliases.add(alias);
                    } else {
                        Set var15 = credentials.getIssuerX500Principals();

                        for(int var16 = 0; var16 < var13.length; ++var16) {
                            if(var15.contains(((Object[])issuers)[var16])) {
                                aliases.add(alias);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isCertValid(X509Certificate cert) {
        if(this.cv != null) {
            try {
                CertVerifyResult e = this.cv.verify(cert, new Date());
                return e.isCertValid();
            } catch (InvalidAlgorithmParameterException var3) {
                throw new AvLoginException(var3);
            }
        } else {
            return true;
        }
    }

    private Map<String, by.avest.edoc.client.PersonalKeyManager2.X509Credentials> getCredentials() {
        if(this.credentials == null) {
            this.credentials = new HashMap();
            Enumeration aliases = null;

            try {
                aliases = this.ks.aliases();
            } catch (KeyStoreException var6) {
                throw new AvLoginException(var6);
            }

            while(aliases.hasMoreElements()) {
                String alias = (String)aliases.nextElement();

                try {
                    if(this.ks.isKeyEntry(alias)) {
                        Object e = this.ks.getCertificateChain(alias);
                        if(e != null && ((Object[])e).length != 0 && ((Object[])e)[0] instanceof X509Certificate) {
                            if(!(e instanceof X509Certificate[])) {
                                X509Certificate[] cred = new X509Certificate[((Object[])e).length];
                                System.arraycopy(e, 0, cred, 0, ((Object[])e).length);
                                e = cred;
                            }

                            by.avest.edoc.client.PersonalKeyManager2.X509Credentials cred1 = new by.avest.edoc.client.PersonalKeyManager2.X509Credentials((X509Certificate[])((X509Certificate[])e));
                            this.credentials.put(alias, cred1);
                        }
                    }
                } catch (KeyStoreException ex) {
                    log.error(ex.getMessage(), ex);
                }
            }
        }

        return this.credentials;
    }

    private static X500Principal[] convertPrincipals(Principal[] principals) {
        ArrayList list = new ArrayList(principals.length);
        Principal[] arr$ = principals;
        int len$ = principals.length;

        for(int i$ = 0; i$ < len$; ++i$) {
            Principal p = arr$[i$];
            if(p instanceof X500Principal) {
                list.add((X500Principal)p);
            } else {
                try {
                    list.add(new X500Principal(p.getName()));
                } catch (IllegalArgumentException var7) {
                    log.error(var7.getMessage(), var7);
                }
            }
        }

        return (X500Principal[])list.toArray(new X500Principal[list.size()]);
    }

    public String chooseClientAlias(String[] keytypes, Principal[] issuers, Socket socket) {
        if(this.alias == null) {
            if(keytypes == null) {
                return null;
            }

            List types = Arrays.asList(keytypes);
            boolean containsBign = types.contains("1.2.112.0.2.0.34.101.45.2.1") || types.contains("Bign");
            if(!containsBign) {
                HashSet aliases = new HashSet();
                aliases.addAll(types);
                aliases.addAll(Arrays.asList(DEFAULT_KEY_TYPES));
                keytypes = (String[])aliases.toArray(new String[0]);
            }

            ArrayList var13 = new ArrayList();
            String[] aliasesStrings = keytypes;
            int e = keytypes.length;

            for(int i$ = 0; i$ < e; ++i$) {
                String keyType = aliasesStrings[i$];
                String[] clientAliases = this.getClientAliases(keyType, issuers);
                if(clientAliases != null && clientAliases.length > 0) {
                    var13.addAll(Arrays.asList(clientAliases));
                }
            }

            aliasesStrings = (String[])var13.toArray(new String[0]);
            if(aliasesStrings.length == 0) {
                return null;
            }

            if(aliasesStrings.length == 1) {
                this.alias = aliasesStrings[0];
            } else {
                try {
                    this.alias = this.chooseAlias(aliasesStrings);
                } catch (IOException var12) {
                    throw new AvLoginException("При выборе контейнера с ключом клиента произошла ошибка.", var12);
                }
            }
        }

        return this.alias;
    }

    public abstract String chooseAlias(String[] var1) throws IOException;

    public PrivateKey getPrivateKey(String alias) {
        try {
            if(this.alias == null || !this.alias.equalsIgnoreCase(alias) || this.privkey == null) {
                this.alias = alias;
                this.privkey = (PrivateKey)this.ks.getKey(alias, this.getPassword(alias));
            }
        } catch (KeyStoreException var3) {
            throw new AvLoginException(var3);
        } catch (UnrecoverableKeyException var4) {
            throw new AvLoginException(var4);
        } catch (NoSuchAlgorithmException var5) {
            throw new AvLoginException(var5);
        } catch (IOException var6) {
            throw new AvLoginException(var6);
        }

        return this.privkey;
    }

    private char[] getPassword(String alias) throws IOException {
        if(this.alias == null || !this.alias.equalsIgnoreCase(alias) || this.password == null) {
            this.password = this.promptPassword(alias);
        }

        return this.password;
    }

    public abstract char[] promptPassword(String var1) throws IOException;

    public X509Certificate[] getCertificateChain(String alias) {
        try {
            if(this.alias == null || !this.alias.equalsIgnoreCase(alias) || this.certchain == null) {
                this.alias = alias;
                this.certchain = (X509Certificate[])((X509Certificate[])this.ks.getCertificateChain(alias));
            }
        } catch (KeyStoreException var3) {
            throw new AvLoginException(var3);
        }

        return this.certchain;
    }

    private static String parseUnpExtValue(X509Certificate cert, String unpOid) throws IOException {
        String result = null;
        byte[] oidDer = cert.getExtensionValue(unpOid);
        if(oidDer == null) {
            return result;
        } else {
            DerValue oidOctet = new DerValue(oidDer);
            DerInputStream dis = oidOctet.getData();
            result = dis.getBMPString();
            return result;
        }
    }

    public static String bytes2Hex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];

        for(int j = 0; j < bytes.length; ++j) {
            int v = bytes[j] & 255;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 15];
        }

        return new String(hexChars);
    }

    private static String parsePubKeyIdExtValue(X509Certificate cert, String unpOid) throws IOException {
        String result = null;
        byte[] oidDer = cert.getExtensionValue(unpOid);
        if(oidDer == null) {
            return result;
        } else {
            DerValue oidOctet = new DerValue(oidDer);
            DerInputStream dis = oidOctet.getData();
            byte[] pubkeyid = dis.getOctetString();
            result = bytes2Hex(pubkeyid);
            return result;
        }
    }

    private static String parseKeyID(X509Certificate cert) throws IOException {
        return parsePubKeyIdExtValue(cert, "2.5.29.14");
    }

    private static String parseUNP(X509Certificate cert) throws IOException {
        String result = null;
        String gossuokUnp = parseUnpExtValue(cert, "1.2.112.1.2.1.1.1.1.2");
        String rupiicUnp = parseUnpExtValue(cert, "1.3.6.1.4.1.12656.106.101");
        if(gossuokUnp != null && rupiicUnp != null) {
            if(!gossuokUnp.equalsIgnoreCase(rupiicUnp)) {
                throw new AvLoginException("Сертификат пользователя содержит различные значения УНП: " + gossuokUnp + " и " + rupiicUnp + ".");
            }

            result = gossuokUnp;
        } else if(gossuokUnp != null) {
            result = gossuokUnp;
        } else if(rupiicUnp != null) {
            result = rupiicUnp;
        } else {
            result = null;
        }

        return result;
    }

    public String chooseServerAlias(String keyType, Principal[] issuers, Socket socket) {
        throw new UnsupportedOperationException();
    }

    public String[] getServerAliases(String keyType, Principal[] issuers) {
        throw new UnsupportedOperationException();
    }

    void reset() {
        if(this.password != null) {
            for(int i = 0; i < this.password.length; ++i) {
                this.password[i] = 32;
            }

            this.password = null;
        }

        this.password = null;
        this.alias = null;
        this.privkey = null;
        this.certchain = null;
    }

    private static class X509Credentials {
        X509Certificate[] certificates;
        private Set<X500Principal> issuerX500Principals;

        X509Credentials(X509Certificate[] certificates) {
            this.certificates = certificates;
        }

        synchronized Set<X500Principal> getIssuerX500Principals() {
            if(this.issuerX500Principals == null) {
                this.issuerX500Principals = new HashSet();
                X509Certificate[] arr$ = this.certificates;
                int len$ = arr$.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    X509Certificate certificate = arr$[i$];
                    this.issuerX500Principals.add(certificate.getIssuerX500Principal());
                }
            }

            return this.issuerX500Principals;
        }
    }
}
