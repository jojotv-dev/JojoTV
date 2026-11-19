package ql;

import kotlin.UShort;

/* JADX INFO: loaded from: classes5.dex */
public final class c extends bm.a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final byte[] f50936e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final byte[] f50937i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final byte[] f50938m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public final byte[] f50939n;

    public c(b bVar, byte[] bArr, byte[] bArr2, byte[] bArr3, byte[] bArr4) {
        super(true, (Object) bVar);
        this.f50937i = rn.d.b(bArr);
        this.f50938m = rn.d.b(bArr2);
        this.f50939n = rn.d.b(bArr3);
        this.f50936e = rn.d.b(bArr4);
    }

    /* JADX WARN: Code duplicated, block: B:60:0x017a  */
    /* JADX WARN: Code duplicated, block: B:62:0x017f  */
    public final byte[] a() {
        int i10;
        byte[] bArr = this.f50936e;
        if (bArr != null && bArr.length != 0) {
            return rn.d.b(bArr);
        }
        int i11 = ((b) this.f6278c).f50935c;
        int i12 = 1 << i11;
        byte b5 = a.f50929a[i11];
        byte[] bArr2 = new byte[i12];
        byte[] bArr3 = new byte[i12];
        byte[] bArr4 = this.f50937i;
        if (a.c(bArr2, i11, b5, bArr4, bArr4.length) == 0) {
            throw new IllegalStateException("unable to decode f");
        }
        byte[] bArr5 = this.f50938m;
        if (a.c(bArr3, i11, b5, bArr5, bArr5.length) == 0) {
            throw new IllegalStateException("unable to decode g");
        }
        short[] sArr = new short[i12];
        short[] sArr2 = new short[i12];
        for (int i13 = 0; i13 < i12; i13++) {
            byte b10 = bArr2[i13];
            sArr2[i13] = (short) (b10 + ((-(b10 >>> 31)) & 12289));
            byte b11 = bArr3[i13];
            sArr[i13] = (short) (b11 + (12289 & (-(b11 >>> 31))));
        }
        a.a(sArr, i11);
        a.a(sArr2, i11);
        for (int i14 = 0; i14 < i12; i14++) {
            short s2 = sArr2[i14];
            if (s2 == 0) {
                throw new IllegalStateException("unable to recover public key: f not invertible mod q");
            }
            short s4 = sArr[i14];
            int iB = a.b(s2, 10952);
            int iB2 = a.b(iB, iB);
            int iB3 = a.b(iB2, iB);
            int iB4 = a.b(iB3, iB2);
            int iB5 = a.b(iB4, iB4);
            int iB6 = a.b(iB5, iB5);
            int iB7 = a.b(iB6, iB6);
            int iB8 = a.b(iB7, iB7);
            int iB9 = a.b(iB8, iB8);
            int iB10 = a.b(iB9, iB3);
            int iB11 = a.b(iB10, iB9);
            int iB12 = a.b(iB11, iB11);
            int iB13 = a.b(a.b(iB12, iB12), iB10);
            int iB14 = a.b(iB13, iB13);
            int iB15 = a.b(a.b(iB14, iB14), iB11);
            sArr[i14] = (short) a.b(a.b(a.b(iB15, iB15), iB), s4);
        }
        int i15 = 1;
        int i16 = i12;
        while (true) {
            short s10 = UShort.MAX_VALUE;
            if (i16 <= 1) {
                break;
            }
            i16 >>= 1;
            int i17 = i15 << 1;
            int i18 = 0;
            int i19 = 0;
            while (i18 < i16) {
                int i20 = i19 + i15;
                short s11 = a.f50931c[i16 + i18];
                int i21 = i19;
                while (i21 < i20) {
                    int i22 = sArr[i21] & s10;
                    int i23 = i21 + i15;
                    int i24 = sArr[i23] & s10;
                    short s12 = s10;
                    int i25 = (i22 + i24) - 12289;
                    sArr[i21] = (short) (i25 + ((-(i25 >>> 31)) & 12289));
                    int i26 = i22 - i24;
                    sArr[i23] = (short) a.b(i26 + ((-(i26 >>> 31)) & 12289), s11);
                    i21++;
                    s10 = s12;
                }
                i18++;
                i19 += i17;
            }
            i15 = i17;
        }
        int i27 = 4091;
        for (int i28 = i12; i28 > 1; i28 >>= 1) {
            i27 = (i27 + ((-(i27 & 1)) & 12289)) >>> 1;
        }
        for (int i29 = 0; i29 < i12; i29++) {
            sArr[i29] = (short) a.b(sArr[i29], i27);
        }
        int i30 = i12 * 14;
        int i31 = i30 / 8;
        int i32 = i31 + 1;
        byte[] bArr6 = new byte[i32];
        int i33 = 0;
        while (true) {
            if (i33 >= i12) {
                int i34 = (i30 + 7) >> 3;
                if (i34 > i31) {
                    break;
                }
                int i35 = 1;
                int i36 = 0;
                int i37 = 0;
                int i38 = 0;
                while (i36 < i12) {
                    i37 = (i37 << 14) | (sArr[i36] & UShort.MAX_VALUE);
                    int i39 = i38 + 14;
                    while (i39 >= 8) {
                        i39 -= 8;
                        bArr6[i35] = (byte) (i37 >> i39);
                        i35++;
                    }
                    i36++;
                    i38 = i39;
                }
                if (i38 > 0) {
                    bArr6[i35] = (byte) (i37 << (8 - i38));
                }
                i10 = i34;
                if (i10 == i31) {
                    return rn.d.h(bArr6, 1, i32);
                }
                throw new IllegalStateException("public key encoding failed");
            }
            if ((sArr[i33] & UShort.MAX_VALUE) >= 12289) {
                break;
            }
            i33++;
        }
        i10 = 0;
        if (i10 == i31) {
            return rn.d.h(bArr6, 1, i32);
        }
        throw new IllegalStateException("public key encoding failed");
    }
}
