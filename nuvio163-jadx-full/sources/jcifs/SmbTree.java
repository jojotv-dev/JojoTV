package jcifs;

/* JADX INFO: loaded from: classes5.dex */
public interface SmbTree extends AutoCloseable {
    @Override // java.lang.AutoCloseable
    void close();

    <T extends SmbTree> T unwrap(Class<T> cls);
}
