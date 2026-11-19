package jcifs.internal.dfs;

import java.util.Map;
import jcifs.DfsReferralData;

/* JADX INFO: loaded from: classes5.dex */
public interface DfsReferralDataInternal extends DfsReferralData {
    void append(DfsReferralDataInternal dfsReferralDataInternal);

    DfsReferralDataInternal combine(DfsReferralData dfsReferralData);

    void fixupDomain(String str);

    void fixupHost(String str);

    String getKey();

    boolean isIntermediate();

    boolean isResolveHashes();

    @Override // jcifs.DfsReferralData
    DfsReferralDataInternal next();

    void replaceCache();

    void setCacheMap(Map<String, DfsReferralDataInternal> map);

    void setKey(String str);

    void setLink(String str);

    void stripPathConsumed(int i10);
}
