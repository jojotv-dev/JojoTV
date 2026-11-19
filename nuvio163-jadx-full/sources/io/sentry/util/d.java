package io.sentry.util;

import java.io.Writer;

/* JADX INFO: loaded from: classes5.dex */
public final class d extends Writer {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public long f26256b = 0;

    public static int g(char c5) {
        if (c5 <= 127) {
            return 1;
        }
        return (c5 > 2047 && !Character.isSurrogate(c5)) ? 3 : 2;
    }

    @Override // java.io.Writer, java.io.Closeable, java.lang.AutoCloseable
    public final void close() {
    }

    @Override // java.io.Writer, java.io.Flushable
    public final void flush() {
    }

    @Override // java.io.Writer
    public final void write(char[] cArr, int i10, int i11) {
        for (int i12 = i10; i12 < i10 + i11; i12++) {
            this.f26256b += (long) g(cArr[i12]);
        }
    }

    @Override // java.io.Writer
    public final void write(int i10) {
        this.f26256b += (long) g((char) i10);
    }

    @Override // java.io.Writer
    public final void write(String str, int i10, int i11) {
        for (int i12 = i10; i12 < i10 + i11; i12++) {
            this.f26256b += (long) g(str.charAt(i12));
        }
    }
}
