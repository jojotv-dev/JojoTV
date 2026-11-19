package kk;

import ek.r;

/* JADX INFO: loaded from: classes5.dex */
public final class p implements ek.a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public byte[] f31599a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public byte[] f31600b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public byte[] f31601c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public byte[] f31602d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public ek.a f31603e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public int f31604f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public int f31605g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public boolean f31606h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public boolean f31607i;

    @Override // ek.a
    public final void a(boolean z2, ek.d dVar) {
        ek.a aVar = this.f31603e;
        byte[] bArr = this.f31599a;
        this.f31606h = z2;
        if (!(dVar instanceof nk.i)) {
            reset();
            aVar.a(true, dVar);
            return;
        }
        nk.i iVar = (nk.i) dVar;
        byte[] bArr2 = iVar.f42904b;
        if (bArr2.length < bArr.length) {
            System.arraycopy(bArr2, 0, bArr, bArr.length - bArr2.length, bArr2.length);
            for (int i10 = 0; i10 < bArr.length - bArr2.length; i10++) {
                bArr[i10] = 0;
            }
        } else {
            System.arraycopy(bArr2, 0, bArr, 0, bArr.length);
        }
        reset();
        aVar.a(true, iVar.f42905c);
    }

    @Override // ek.a
    public final int b(byte[] bArr, int i10, int i11, byte[] bArr2) {
        int i12 = this.f31605g;
        byte[] bArr3 = this.f31600b;
        ek.a aVar = this.f31603e;
        byte[] bArr4 = this.f31601c;
        int i13 = 0;
        if (!this.f31607i) {
            if (this.f31606h) {
                if (i10 + i12 > bArr.length) {
                    throw new ek.k("input buffer too short");
                }
                if (i11 + i12 > bArr2.length) {
                    throw new r("output buffer too short");
                }
                aVar.b(bArr3, 0, 0, bArr4);
                for (int i14 = 0; i14 < i12; i14++) {
                    bArr2[i11 + i14] = d(bArr[i10 + i14], i14);
                }
                while (i13 < i12) {
                    bArr3[i13] = bArr2[i11 + i13];
                    i13++;
                }
                return i12;
            }
            if (i10 + i12 > bArr.length) {
                throw new ek.k("input buffer too short");
            }
            if (i11 + i12 > bArr2.length) {
                throw new r("output buffer too short");
            }
            aVar.b(bArr3, 0, 0, bArr4);
            for (int i15 = 0; i15 < i12; i15++) {
                bArr2[i11 + i15] = d(bArr[i10 + i15], i15);
            }
            while (i13 < i12) {
                bArr3[i13] = bArr[i10 + i13];
                i13++;
            }
            return i12;
        }
        if (this.f31606h) {
            byte[] bArr5 = this.f31599a;
            if (i10 + i12 > bArr.length) {
                throw new ek.k("input buffer too short");
            }
            int i16 = this.f31604f;
            if (i16 != 0) {
                if (i16 < i12 + 2) {
                    return i12;
                }
                if (i11 + i12 > bArr2.length) {
                    throw new r("output buffer too short");
                }
                aVar.b(bArr3, 0, 0, bArr4);
                for (int i17 = 0; i17 < i12; i17++) {
                    bArr2[i11 + i17] = d(bArr[i10 + i17], i17);
                }
                System.arraycopy(bArr2, i11, bArr3, 0, i12);
                return i12;
            }
            int i18 = i12 * 2;
            if (i18 + i11 + 2 > bArr2.length) {
                throw new r("output buffer too short");
            }
            aVar.b(bArr3, 0, 0, bArr4);
            for (int i19 = 0; i19 < i12; i19++) {
                bArr2[i11 + i19] = d(bArr5[i19], i19);
            }
            System.arraycopy(bArr2, i11, bArr3, 0, i12);
            aVar.b(bArr3, 0, 0, bArr4);
            int i20 = i11 + i12;
            bArr2[i20] = d(bArr5[i12 - 2], 0);
            bArr2[i20 + 1] = d(bArr5[i12 - 1], 1);
            System.arraycopy(bArr2, i11 + 2, bArr3, 0, i12);
            aVar.b(bArr3, 0, 0, bArr4);
            for (int i21 = 0; i21 < i12; i21++) {
                bArr2[i20 + 2 + i21] = d(bArr[i10 + i21], i21);
            }
            System.arraycopy(bArr2, i20 + 2, bArr3, 0, i12);
            int i22 = i18 + 2;
            this.f31604f += i22;
            return i22;
        }
        byte[] bArr6 = this.f31602d;
        if (i10 + i12 > bArr.length) {
            throw new ek.k("input buffer too short");
        }
        if (i11 + i12 > bArr2.length) {
            throw new r("output buffer too short");
        }
        int i23 = this.f31604f;
        if (i23 == 0) {
            for (int i24 = 0; i24 < i12; i24++) {
                bArr3[i24] = bArr[i10 + i24];
            }
            aVar.b(bArr3, 0, 0, bArr4);
            this.f31604f += i12;
            return 0;
        }
        if (i23 == i12) {
            System.arraycopy(bArr, i10, bArr6, 0, i12);
            int i25 = i12 - 2;
            System.arraycopy(bArr3, 2, bArr3, 0, i25);
            bArr3[i25] = bArr6[0];
            bArr3[i12 - 1] = bArr6[1];
            aVar.b(bArr3, 0, 0, bArr4);
            for (int i26 = 0; i26 < i25; i26++) {
                bArr2[i11 + i26] = d(bArr6[i26 + 2], i26);
            }
            System.arraycopy(bArr6, 2, bArr3, 0, i25);
            this.f31604f += 2;
            return i25;
        }
        if (i23 >= i12 + 2) {
            System.arraycopy(bArr, i10, bArr6, 0, i12);
            int i27 = i12 - 2;
            bArr2[i11] = d(bArr6[0], i27);
            bArr2[i11 + 1] = d(bArr6[1], i12 - 1);
            System.arraycopy(bArr6, 0, bArr3, i27, 2);
            aVar.b(bArr3, 0, 0, bArr4);
            for (int i28 = 0; i28 < i27; i28++) {
                bArr2[i11 + i28 + 2] = d(bArr6[i28 + 2], i28);
            }
            System.arraycopy(bArr6, 2, bArr3, 0, i27);
        }
        return i12;
    }

    @Override // ek.a
    public final int c() {
        return this.f31603e.c();
    }

    public final byte d(byte b5, int i10) {
        return (byte) (b5 ^ this.f31601c[i10]);
    }

    @Override // ek.a
    public final String getAlgorithmName() {
        StringBuilder sb2;
        String str;
        ek.a aVar = this.f31603e;
        if (this.f31607i) {
            sb2 = new StringBuilder();
            sb2.append(aVar.getAlgorithmName());
            str = "/PGPCFBwithIV";
        } else {
            sb2 = new StringBuilder();
            sb2.append(aVar.getAlgorithmName());
            str = "/PGPCFB";
        }
        sb2.append(str);
        return sb2.toString();
    }

    @Override // ek.a
    public final void reset() {
        this.f31604f = 0;
        int i10 = 0;
        while (true) {
            byte[] bArr = this.f31600b;
            if (i10 == bArr.length) {
                this.f31603e.reset();
                return;
            }
            if (this.f31607i) {
                bArr[i10] = 0;
            } else {
                bArr[i10] = this.f31599a[i10];
            }
            i10++;
        }
    }
}
