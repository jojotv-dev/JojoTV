package jcifs.smb;

import jcifs.DfsReferralData;

/* JADX INFO: loaded from: classes5.dex */
public class DfsReferral extends SmbException {
    private static final long serialVersionUID = 1486630733410281686L;
    private final DfsReferralData data;

    public DfsReferral(DfsReferralData dfsReferralData) {
        this.data = dfsReferralData;
    }

    public DfsReferralData getData() {
        return this.data;
    }

    @Override // java.lang.Throwable
    public String toString() {
        return this.data.toString();
    }
}
