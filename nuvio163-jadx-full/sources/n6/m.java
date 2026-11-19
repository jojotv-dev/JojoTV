package n6;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
public final class m extends InputStream {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final InputStream f39526b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f39527c = 1073741824;

    public m(InputStream inputStream) {
        this.f39526b = inputStream;
    }

    @Override // java.io.InputStream
    public final int available() {
        return this.f39527c;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public final void close() throws IOException {
        this.f39526b.close();
    }

    @Override // java.io.InputStream
    public final int read() throws IOException {
        int i10 = this.f39526b.read();
        if (i10 == -1) {
            this.f39527c = 0;
        }
        return i10;
    }

    @Override // java.io.InputStream
    public final long skip(long j10) {
        return this.f39526b.skip(j10);
    }

    @Override // java.io.InputStream
    public final int read(byte[] bArr) throws IOException {
        int i10 = this.f39526b.read(bArr);
        if (i10 == -1) {
            this.f39527c = 0;
        }
        return i10;
    }

    @Override // java.io.InputStream
    public final int read(byte[] bArr, int i10, int i11) throws IOException {
        int i12 = this.f39526b.read(bArr, i10, i11);
        if (i12 == -1) {
            this.f39527c = 0;
        }
        return i12;
    }
}
