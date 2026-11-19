package jcifs.smb;

import jcifs.SmbSession;
import jcifs.SmbTransport;
import jcifs.SmbTree;

/* JADX INFO: loaded from: classes5.dex */
public interface SmbSessionInternal extends SmbSession {
    byte[] getSessionKey();

    SmbTree getSmbTree(String str, String str2);

    SmbTransport getTransport();

    boolean isInUse();

    void reauthenticate();

    void treeConnectLogon();
}
