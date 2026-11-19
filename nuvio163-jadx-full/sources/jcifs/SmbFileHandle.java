package jcifs;

/* JADX INFO: loaded from: classes5.dex */
public interface SmbFileHandle extends AutoCloseable {
    @Override // java.lang.AutoCloseable
    void close();

    void close(long j10);

    long getInitialSize();

    SmbTreeHandle getTree();

    boolean isValid();

    void release();
}
