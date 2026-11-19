package re;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class p1 extends FilterInputStream {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public long f53190b;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public p1(InputStream input, long j10) {
        super(input);
        Intrinsics.checkNotNullParameter(input, "input");
        this.f53190b = j10;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public final int read() throws IOException {
        if (this.f53190b <= 0) {
            return -1;
        }
        int i10 = super.read();
        if (i10 >= 0) {
            this.f53190b--;
        }
        return i10;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public final int read(byte[] buffer, int i10, int i11) throws IOException {
        Intrinsics.checkNotNullParameter(buffer, "buffer");
        long j10 = this.f53190b;
        if (j10 <= 0) {
            return -1;
        }
        int i12 = super.read(buffer, i10, (int) Math.min(i11, j10));
        if (i12 > 0) {
            this.f53190b -= (long) i12;
        }
        return i12;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public final void close() {
    }
}
