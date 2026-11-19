package jcifs;

import java.io.InputStream;
import java.io.OutputStream;

/* JADX INFO: loaded from: classes5.dex */
public interface SmbPipeHandle extends AutoCloseable {
    @Override // java.lang.AutoCloseable
    void close();

    InputStream getInput();

    OutputStream getOutput();

    SmbPipeResource getPipe();

    boolean isOpen();

    boolean isStale();

    <T extends SmbPipeHandle> T unwrap(Class<T> cls);
}
