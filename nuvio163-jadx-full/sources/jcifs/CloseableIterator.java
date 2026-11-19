package jcifs;

import java.util.Iterator;

/* JADX INFO: loaded from: classes5.dex */
public interface CloseableIterator<T> extends Iterator<T>, AutoCloseable {
    @Override // java.lang.AutoCloseable
    void close();
}
