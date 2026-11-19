package gk;

import ek.v;

/* JADX INFO: loaded from: classes5.dex */
public final class f implements v {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public byte[] f20280a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f20281b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f20282c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public byte[] f20283d;

    public final void a(byte[] bArr) {
        this.f20283d = bArr;
        this.f20281b = 0;
        this.f20282c = 0;
        if (this.f20280a == null) {
            this.f20280a = new byte[256];
        }
        for (int i10 = 0; i10 < 256; i10++) {
            this.f20280a[i10] = (byte) i10;
        }
        int length = 0;
        int i11 = 0;
        for (int i12 = 0; i12 < 256; i12++) {
            int i13 = bArr[length] & 255;
            byte[] bArr2 = this.f20280a;
            byte b5 = bArr2[i12];
            i11 = (i13 + b5 + i11) & 255;
            bArr2[i12] = bArr2[i11];
            bArr2[i11] = b5;
            length = (length + 1) % bArr.length;
        }
    }
}
