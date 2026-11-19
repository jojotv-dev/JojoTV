package fe;

import java.io.FileInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class g4 extends FilterInputStream {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Function1 f16897b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public long f16898c;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public g4(FileInputStream inner, Function1 onProgress) {
        super(inner);
        Intrinsics.checkNotNullParameter(inner, "inner");
        Intrinsics.checkNotNullParameter(onProgress, "onProgress");
        this.f16897b = onProgress;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public final int read() throws IOException {
        int i10 = super.read();
        if (i10 >= 0) {
            long j10 = this.f16898c + 1;
            this.f16898c = j10;
            this.f16897b.invoke(Long.valueOf(j10));
        }
        return i10;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public final long skip(long j10) throws IOException {
        long jSkip = super.skip(j10);
        this.f16898c += jSkip;
        return jSkip;
    }

    @Override // java.io.FilterInputStream, java.io.InputStream
    public final int read(byte[] b5, int i10, int i11) throws IOException {
        Intrinsics.checkNotNullParameter(b5, "b");
        int i12 = super.read(b5, i10, i11);
        if (i12 > 0) {
            long j10 = this.f16898c + ((long) i12);
            this.f16898c = j10;
            this.f16897b.invoke(Long.valueOf(j10));
        }
        return i12;
    }
}
