package jcifs.smb;

import jcifs.SmbSession;
import jcifs.SmbTreeHandle;

/* JADX INFO: loaded from: classes5.dex */
public interface SmbTreeHandleInternal extends SmbTreeHandle {
    boolean areSignaturesActive();

    void ensureDFSResolved();

    int getMaximumBufferSize();

    int getReceiveBufferSize();

    int getSendBufferSize();

    SmbSession getSession();

    boolean hasCapability(int i10);

    void release();
}
