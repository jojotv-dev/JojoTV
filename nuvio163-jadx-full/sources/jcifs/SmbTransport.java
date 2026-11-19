package jcifs;

/* JADX INFO: loaded from: classes5.dex */
public interface SmbTransport extends AutoCloseable {
    @Override // java.lang.AutoCloseable
    void close();

    CIFSContext getContext();

    Address getRemoteAddress();

    String getRemoteHostName();

    <T extends SmbTransport> T unwrap(Class<T> cls);
}
