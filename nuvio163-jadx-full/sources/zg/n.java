package zg;

import java.net.Socket;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import javax.net.ssl.X509ExtendedKeyManager;

/* JADX INFO: loaded from: classes5.dex */
public final class n extends X509ExtendedKeyManager {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ i f66770a;

    public n(i iVar) {
        this.f66770a = iVar;
    }

    @Override // javax.net.ssl.X509KeyManager
    public final String chooseClientAlias(String[] strArr, Principal[] principalArr, Socket socket) {
        for (String str : strArr) {
            if (str.equals("RSA")) {
                return "key";
            }
        }
        return null;
    }

    @Override // javax.net.ssl.X509KeyManager
    public final String chooseServerAlias(String str, Principal[] principalArr, Socket socket) {
        return null;
    }

    @Override // javax.net.ssl.X509KeyManager
    public final X509Certificate[] getCertificateChain(String str) {
        if ("key".equals(str)) {
            return new X509Certificate[]{(X509Certificate) this.f66770a.f66746b};
        }
        return null;
    }

    @Override // javax.net.ssl.X509KeyManager
    public final String[] getClientAliases(String str, Principal[] principalArr) {
        return null;
    }

    @Override // javax.net.ssl.X509KeyManager
    public final PrivateKey getPrivateKey(String str) {
        if ("key".equals(str)) {
            return this.f66770a.f66745a;
        }
        return null;
    }

    @Override // javax.net.ssl.X509KeyManager
    public final String[] getServerAliases(String str, Principal[] principalArr) {
        return null;
    }
}
