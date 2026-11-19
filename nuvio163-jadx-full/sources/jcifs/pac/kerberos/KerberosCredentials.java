package jcifs.pac.kerberos;

import java.util.ArrayList;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.login.LoginContext;

/* JADX INFO: loaded from: classes5.dex */
public class KerberosCredentials {
    private Subject subject;

    public KerberosCredentials() {
        this(System.getProperty("jaaslounge.sso.jaas.config"));
    }

    public KerberosKey getKey(int i10) {
        KerberosKey kerberosKey = null;
        for (Object obj : this.subject.getPrivateCredentials()) {
            if (obj instanceof KerberosKey) {
                KerberosKey kerberosKey2 = (KerberosKey) obj;
                if (kerberosKey2.getKeyType() == i10) {
                    kerberosKey = kerberosKey2;
                }
            }
        }
        return kerberosKey;
    }

    public KerberosKey[] getKeys() {
        ArrayList arrayList = new ArrayList();
        for (Object obj : this.subject.getPrivateCredentials()) {
            if (obj instanceof KerberosKey) {
                arrayList.add((KerberosKey) obj);
            }
        }
        return (KerberosKey[]) arrayList.toArray(new KerberosKey[0]);
    }

    public Subject getSubject() {
        return this.subject;
    }

    public KerberosCredentials(String str) {
        LoginContext loginContext = new LoginContext(str);
        loginContext.login();
        this.subject = loginContext.getSubject();
    }
}
