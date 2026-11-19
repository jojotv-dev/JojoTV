package z4;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes3.dex */
public final class f extends b {
    public f(byte[] bArr) {
        super(bArr);
        this.f65481b.mark(Integer.MAX_VALUE);
    }

    public final void seek(long j10) throws IOException {
        int i10 = this.f65482c;
        if (i10 > j10) {
            this.f65482c = 0;
            this.f65481b.reset();
        } else {
            j10 -= (long) i10;
        }
        g((int) j10);
    }

    public f(InputStream inputStream) {
        super(inputStream);
        if (inputStream.markSupported()) {
            this.f65481b.mark(Integer.MAX_VALUE);
            return;
        }
        throw new IllegalArgumentException("Cannot create SeekableByteOrderedDataInputStream with stream that does not support mark/reset");
    }
}
