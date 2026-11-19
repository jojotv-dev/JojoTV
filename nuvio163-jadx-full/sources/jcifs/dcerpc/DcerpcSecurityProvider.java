package jcifs.dcerpc;

import jcifs.dcerpc.ndr.NdrBuffer;

/* JADX INFO: loaded from: classes5.dex */
public interface DcerpcSecurityProvider {
    void unwrap(NdrBuffer ndrBuffer);

    void wrap(NdrBuffer ndrBuffer);
}
