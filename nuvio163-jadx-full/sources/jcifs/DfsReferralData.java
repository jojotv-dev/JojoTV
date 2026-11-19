package jcifs;

/* JADX INFO: loaded from: classes5.dex */
public interface DfsReferralData {
    String getDomain();

    long getExpiration();

    String getLink();

    String getPath();

    int getPathConsumed();

    String getServer();

    String getShare();

    DfsReferralData next();

    <T extends DfsReferralData> T unwrap(Class<T> cls);
}
