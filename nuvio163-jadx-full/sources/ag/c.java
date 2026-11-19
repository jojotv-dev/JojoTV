package ag;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class c extends ByteArrayOutputStream {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f1708b;

    public byte[] A() {
        switch (this.f1708b) {
            case 4:
                break;
        }
        return ((ByteArrayOutputStream) this).buf;
    }

    public void E() {
        switch (this.f1708b) {
            case 0:
                Arrays.fill(((ByteArrayOutputStream) this).buf, (byte) 0);
                reset();
                break;
            case 1:
                Arrays.fill(((ByteArrayOutputStream) this).buf, (byte) 0);
                reset();
                break;
            case 2:
                Arrays.fill(((ByteArrayOutputStream) this).buf, (byte) 0);
                reset();
                break;
            default:
                Arrays.fill(((ByteArrayOutputStream) this).buf, (byte) 0);
                reset();
                break;
        }
    }

    @Override // java.io.ByteArrayOutputStream, java.io.OutputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        switch (this.f1708b) {
            case 7:
                break;
            default:
                super.close();
                break;
        }
    }

    public byte[] g() {
        byte[] buf = ((ByteArrayOutputStream) this).buf;
        Intrinsics.checkNotNullExpressionValue(buf, "buf");
        return buf;
    }

    public void l() {
        Arrays.fill(((ByteArrayOutputStream) this).buf, (byte) 0);
    }

    @Override // java.io.OutputStream
    public void write(byte[] bArr) throws IOException {
        switch (this.f1708b) {
            case 7:
                write(bArr, 0, bArr.length);
                break;
            default:
                super.write(bArr);
                break;
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ c(int i10, int i11) {
        super(i10);
        this.f1708b = i11;
    }

    private final void n() {
    }
}
