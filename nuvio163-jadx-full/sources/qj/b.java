package qj;

import java.io.OutputStream;

/* JADX INFO: loaded from: classes5.dex */
public final class b extends OutputStream {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final byte[] f50801b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f50802c = 1;

    public b(byte[] bArr) {
        this.f50801b = bArr;
    }

    @Override // java.io.OutputStream
    public final void write(int i10) {
        int i11 = this.f50802c;
        this.f50802c = i11 + 1;
        this.f50801b[i11] = (byte) i10;
    }

    @Override // java.io.OutputStream
    public final void write(byte[] bArr, int i10, int i11) {
        System.arraycopy(bArr, i10, this.f50801b, this.f50802c, i11);
        this.f50802c += i11;
    }
}
