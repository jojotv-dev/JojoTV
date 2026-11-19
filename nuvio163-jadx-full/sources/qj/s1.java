package qj;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes5.dex */
public final class s1 extends v1 {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f50895e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public int f50896i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public boolean f50897m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public boolean f50898n;

    public s1(InputStream inputStream, int i10) throws IOException {
        super(inputStream, i10);
        this.f50897m = false;
        this.f50898n = true;
        this.f50895e = inputStream.read();
        int i11 = inputStream.read();
        this.f50896i = i11;
        if (i11 < 0) {
            throw new EOFException();
        }
        l();
    }

    public final boolean l() {
        if (!this.f50897m && this.f50898n && this.f50895e == 0 && this.f50896i == 0) {
            this.f50897m = true;
            g();
        }
        return this.f50897m;
    }

    @Override // java.io.InputStream
    public final int read() throws IOException {
        if (l()) {
            return -1;
        }
        int i10 = this.f50910b.read();
        if (i10 < 0) {
            throw new EOFException();
        }
        int i11 = this.f50895e;
        this.f50895e = this.f50896i;
        this.f50896i = i10;
        return i11;
    }

    @Override // java.io.InputStream
    public final int read(byte[] bArr, int i10, int i11) throws IOException {
        if (this.f50898n || i11 < 3) {
            return super.read(bArr, i10, i11);
        }
        if (this.f50897m) {
            return -1;
        }
        InputStream inputStream = this.f50910b;
        int i12 = inputStream.read(bArr, i10 + 2, i11 - 2);
        if (i12 < 0) {
            throw new EOFException();
        }
        bArr[i10] = (byte) this.f50895e;
        bArr[i10 + 1] = (byte) this.f50896i;
        this.f50895e = inputStream.read();
        int i13 = inputStream.read();
        this.f50896i = i13;
        if (i13 >= 0) {
            return i12 + 2;
        }
        throw new EOFException();
    }
}
