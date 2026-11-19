package z4;

import android.media.MediaDataSource;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public final class a extends MediaDataSource {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public long f65479b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ f f65480c;

    public a(f fVar) {
        this.f65480c = fVar;
    }

    @Override // android.media.MediaDataSource
    public final long getSize() {
        return -1L;
    }

    @Override // android.media.MediaDataSource
    public final int readAt(long j10, byte[] bArr, int i10, int i11) {
        if (i11 == 0) {
            return 0;
        }
        if (j10 < 0) {
            return -1;
        }
        try {
            long j11 = this.f65479b;
            f fVar = this.f65480c;
            if (j11 != j10) {
                if (j11 >= 0 && j10 >= j11 + ((long) fVar.f65481b.available())) {
                    return -1;
                }
                fVar.seek(j10);
                this.f65479b = j10;
            }
            if (i11 > fVar.f65481b.available()) {
                i11 = fVar.f65481b.available();
            }
            int i12 = fVar.read(bArr, i10, i11);
            if (i12 >= 0) {
                this.f65479b += (long) i12;
                return i12;
            }
        } catch (IOException unused) {
        }
        this.f65479b = -1L;
        return -1;
    }

    @Override // java.io.Closeable, java.lang.AutoCloseable
    public final void close() {
    }
}
