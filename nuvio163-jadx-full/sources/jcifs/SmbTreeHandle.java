package jcifs;

/* JADX INFO: loaded from: classes5.dex */
public interface SmbTreeHandle extends AutoCloseable {
    @Override // java.lang.AutoCloseable
    void close();

    Configuration getConfig();

    String getConnectedShare();

    String getOEMDomainName();

    String getRemoteHostName();

    long getServerTimeZoneOffset();

    int getTreeType();

    boolean isConnected();

    boolean isSMB2();

    boolean isSameTree(SmbTreeHandle smbTreeHandle);
}
