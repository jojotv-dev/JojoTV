package jcifs.smb;

import jcifs.DfsReferralData;
import jcifs.SmbResourceLocator;

/* JADX INFO: loaded from: classes5.dex */
public interface SmbResourceLocatorInternal extends SmbResourceLocator {
    String handleDFSReferral(DfsReferralData dfsReferralData, String str);

    boolean overlaps(SmbResourceLocator smbResourceLocator);

    boolean shouldForceSigning();
}
