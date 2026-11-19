package jcifs;

/* JADX INFO: loaded from: classes5.dex */
public interface SmbSession extends AutoCloseable {
    @Override // java.lang.AutoCloseable
    void close();

    Configuration getConfig();

    CIFSContext getContext();

    <T extends SmbSession> T unwrap(Class<T> cls);
}
