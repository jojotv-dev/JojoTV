package kk;

import ek.r;

/* JADX INFO: loaded from: classes5.dex */
public final class o implements ek.a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public byte[] f31592a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public byte[] f31593b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public byte[] f31594c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public ek.a f31595d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f31596e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public int f31597f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public boolean f31598g;

    @Override // ek.a
    public final void a(boolean z2, ek.d dVar) {
        this.f31598g = z2;
        reset();
        this.f31595d.a(true, dVar);
    }

    @Override // ek.a
    public final int b(byte[] bArr, int i10, int i11, byte[] bArr2) {
        int i12 = this.f31597f;
        byte[] bArr3 = this.f31593b;
        ek.a aVar = this.f31595d;
        byte[] bArr4 = this.f31594c;
        int i13 = 0;
        int i14 = 2;
        if (this.f31598g) {
            if (i10 + i12 > bArr.length) {
                throw new ek.k("input buffer too short");
            }
            if (i11 + i12 > bArr2.length) {
                throw new r("output buffer too short");
            }
            int i15 = this.f31596e;
            if (i15 > i12) {
                int i16 = i12 - 2;
                byte bD = d(bArr[i10], i16);
                bArr2[i11] = bD;
                bArr3[i16] = bD;
                int i17 = i12 - 1;
                byte bD2 = d(bArr[i10 + 1], i17);
                bArr2[i11 + 1] = bD2;
                bArr3[i17] = bD2;
                aVar.b(bArr3, 0, 0, bArr4);
                while (i14 < i12) {
                    int i18 = i14 - 2;
                    byte bD3 = d(bArr[i10 + i14], i18);
                    bArr2[i11 + i14] = bD3;
                    bArr3[i18] = bD3;
                    i14++;
                }
            } else {
                if (i15 == 0) {
                    aVar.b(bArr3, 0, 0, bArr4);
                    while (i13 < i12) {
                        byte bD4 = d(bArr[i10 + i13], i13);
                        bArr2[i11 + i13] = bD4;
                        bArr3[i13] = bD4;
                        i13++;
                    }
                    this.f31596e += i12;
                    return i12;
                }
                if (i15 == i12) {
                    aVar.b(bArr3, 0, 0, bArr4);
                    bArr2[i11] = d(bArr[i10], 0);
                    bArr2[i11 + 1] = d(bArr[i10 + 1], 1);
                    int i19 = i12 - 2;
                    System.arraycopy(bArr3, 2, bArr3, 0, i19);
                    System.arraycopy(bArr2, i11, bArr3, i19, 2);
                    aVar.b(bArr3, 0, 0, bArr4);
                    while (i14 < i12) {
                        int i20 = i14 - 2;
                        byte bD5 = d(bArr[i10 + i14], i20);
                        bArr2[i11 + i14] = bD5;
                        bArr3[i20] = bD5;
                        i14++;
                    }
                    this.f31596e += i12;
                }
            }
            return i12;
        }
        if (i10 + i12 > bArr.length) {
            throw new ek.k("input buffer too short");
        }
        if (i11 + i12 > bArr2.length) {
            throw new r("output buffer too short");
        }
        int i21 = this.f31596e;
        if (i21 > i12) {
            byte b5 = bArr[i10];
            int i22 = i12 - 2;
            bArr3[i22] = b5;
            bArr2[i11] = d(b5, i22);
            byte b10 = bArr[i10 + 1];
            int i23 = i12 - 1;
            bArr3[i23] = b10;
            bArr2[i11 + 1] = d(b10, i23);
            aVar.b(bArr3, 0, 0, bArr4);
            while (i14 < i12) {
                byte b11 = bArr[i10 + i14];
                int i24 = i14 - 2;
                bArr3[i24] = b11;
                bArr2[i11 + i14] = d(b11, i24);
                i14++;
            }
        } else {
            if (i21 == 0) {
                aVar.b(bArr3, 0, 0, bArr4);
                while (i13 < i12) {
                    int i25 = i10 + i13;
                    bArr3[i13] = bArr[i25];
                    bArr2[i11 + i13] = d(bArr[i25], i13);
                    i13++;
                }
                this.f31596e += i12;
                return i12;
            }
            if (i21 == i12) {
                aVar.b(bArr3, 0, 0, bArr4);
                byte b12 = bArr[i10];
                byte b13 = bArr[i10 + 1];
                bArr2[i11] = d(b12, 0);
                bArr2[i11 + 1] = d(b13, 1);
                int i26 = i12 - 2;
                System.arraycopy(bArr3, 2, bArr3, 0, i26);
                bArr3[i26] = b12;
                bArr3[i12 - 1] = b13;
                aVar.b(bArr3, 0, 0, bArr4);
                while (i14 < i12) {
                    byte b14 = bArr[i10 + i14];
                    int i27 = i14 - 2;
                    bArr3[i27] = b14;
                    bArr2[i11 + i14] = d(b14, i27);
                    i14++;
                }
                this.f31596e += i12;
            }
        }
        return i12;
    }

    @Override // ek.a
    public final int c() {
        return this.f31595d.c();
    }

    public final byte d(byte b5, int i10) {
        return (byte) (b5 ^ this.f31594c[i10]);
    }

    @Override // ek.a
    public final String getAlgorithmName() {
        return this.f31595d.getAlgorithmName() + "/OpenPGPCFB";
    }

    @Override // ek.a
    public final void reset() {
        this.f31596e = 0;
        byte[] bArr = this.f31592a;
        byte[] bArr2 = this.f31593b;
        System.arraycopy(bArr, 0, bArr2, 0, bArr2.length);
        this.f31595d.reset();
    }
}
