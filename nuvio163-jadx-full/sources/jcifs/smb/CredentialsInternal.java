package jcifs.smb;

import javax.security.auth.Subject;
import jcifs.CIFSContext;
import jcifs.Credentials;

/* JADX INFO: loaded from: classes5.dex */
public interface CredentialsInternal extends Cloneable, Credentials {
    CredentialsInternal clone();

    SSPContext createContext(CIFSContext cIFSContext, String str, String str2, byte[] bArr, boolean z2);

    Subject getSubject();

    void refresh();
}
