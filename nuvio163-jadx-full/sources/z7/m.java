package z7;

import java.io.Writer;

/* JADX INFO: loaded from: classes4.dex */
public final class m extends Writer {
    @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
    public final void close() {
        throw new AssertionError();
    }

    @Override // java.io.Writer, java.io.Flushable
    public final void flush() {
        throw new AssertionError();
    }

    @Override // java.io.Writer
    public final void write(char[] cArr, int i10, int i11) {
        throw new AssertionError();
    }
}
