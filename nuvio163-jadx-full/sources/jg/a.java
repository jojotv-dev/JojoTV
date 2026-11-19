package jg;

import java.util.Arrays;

/* JADX INFO: loaded from: classes4.dex */
public final class a {

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public final int[] f27492l = new int[10];

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final int[] f27493m = new int[10];

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public final int[] f27494n = new int[10];

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public final int[] f27495o = new int[10];

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public final int[] f27496p = new int[10];

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int[] f27482a = new int[10];

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int[] f27484c = new int[10];

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int[] f27486e = new int[10];

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public final int[] f27488g = new int[10];

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final int[] f27490i = new int[10];

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int[] f27483b = new int[10];

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final int[] f27485d = new int[10];

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public final int[] f27489h = new int[10];

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final int[] f27487f = new int[10];

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public final long[] f27491j = new long[20];
    public final int[] k = new int[20];

    public static void b(int i10, int[] iArr, int[] iArr2) {
        int i11 = -i10;
        for (int i12 = 0; i12 < 10; i12++) {
            int i13 = iArr[i12];
            int i14 = (iArr2[i12] ^ i13) & i11;
            iArr[i12] = i13 ^ i14;
            iArr2[i12] = iArr2[i12] ^ i14;
        }
    }

    public static void i(int[] iArr, int[] iArr2, int[] iArr3) {
        int i10 = 0;
        for (int i11 = 0; i11 < 10; i11++) {
            i10 = (iArr2[i11] - iArr3[i11]) - ((i10 >> 26) & 1);
            iArr[i11] = i10 & 67108863;
        }
        int i12 = iArr[0] - ((-((i10 >> 26) & 1)) & 19);
        iArr[0] = i12 & 67108863;
        for (int i13 = 1; i13 < 10; i13++) {
            i12 = iArr[i13] - ((i12 >> 26) & 1);
            iArr[i13] = i12 & 67108863;
        }
        iArr[9] = iArr[9] & 2097151;
    }

    public final void a(int[] iArr, int[] iArr2, int[] iArr3) {
        int i10 = iArr2[0] + iArr3[0];
        iArr[0] = i10 & 67108863;
        for (int i11 = 1; i11 < 10; i11++) {
            i10 = (i10 >> 26) + iArr2[i11] + iArr3[i11];
            iArr[i11] = i10 & 67108863;
        }
        h(iArr);
    }

    public final void c() {
        Arrays.fill(this.f27492l, 0);
        Arrays.fill(this.f27493m, 0);
        Arrays.fill(this.f27494n, 0);
        Arrays.fill(this.f27495o, 0);
        Arrays.fill(this.f27496p, 0);
        Arrays.fill(this.f27482a, 0);
        Arrays.fill(this.f27484c, 0);
        Arrays.fill(this.f27486e, 0);
        Arrays.fill(this.f27488g, 0);
        Arrays.fill(this.f27490i, 0);
        Arrays.fill(this.f27483b, 0);
        Arrays.fill(this.f27485d, 0);
        Arrays.fill(this.f27489h, 0);
        Arrays.fill(this.f27487f, 0);
        Arrays.fill(this.f27491j, 0L);
        Arrays.fill(this.k, 0);
    }

    public final void d(byte[] bArr) {
        int[] iArr;
        int i10;
        int i11 = 31;
        int i12 = bArr[31] | 64;
        int i13 = 6;
        int i14 = 0;
        while (true) {
            int i15 = (i12 >> i13) & 1;
            int i16 = i14 ^ i15;
            int[] iArr2 = this.f27493m;
            int[] iArr3 = this.f27494n;
            b(i16, iArr2, iArr3);
            int[] iArr4 = this.f27495o;
            int[] iArr5 = this.f27496p;
            b(i16, iArr4, iArr5);
            int[] iArr6 = this.f27482a;
            a(iArr6, iArr2, iArr4);
            int[] iArr7 = this.f27483b;
            e(iArr7, iArr6, iArr6);
            int[] iArr8 = this.f27484c;
            i(iArr8, iArr2, iArr4);
            int[] iArr9 = this.f27485d;
            e(iArr9, iArr8, iArr8);
            int[] iArr10 = this.f27490i;
            i(iArr10, iArr7, iArr9);
            int[] iArr11 = this.f27486e;
            a(iArr11, iArr3, iArr5);
            int[] iArr12 = this.f27488g;
            i(iArr12, iArr3, iArr5);
            int i17 = i12;
            int[] iArr13 = this.f27489h;
            e(iArr13, iArr12, iArr6);
            int[] iArr14 = this.f27487f;
            e(iArr14, iArr11, iArr8);
            a(iArr3, iArr13, iArr14);
            e(iArr3, iArr3, iArr3);
            i(iArr5, iArr13, iArr14);
            e(iArr5, iArr5, iArr5);
            e(iArr5, iArr5, this.f27492l);
            e(iArr2, iArr7, iArr9);
            long j10 = 0;
            int i18 = 0;
            while (true) {
                iArr = this.k;
                if (i18 >= 10) {
                    break;
                }
                long j11 = (((long) iArr10[i18]) * 121665) + j10;
                iArr[i18] = ((int) j11) & 67108863;
                j10 = j11 >> 26;
                i18++;
                iArr2 = iArr2;
            }
            int[] iArr15 = iArr2;
            iArr[10] = ((int) j10) & 67108863;
            g(1, iArr4, iArr);
            a(iArr4, iArr4, iArr7);
            e(iArr4, iArr4, iArr10);
            if (i13 > 0) {
                i13--;
                i12 = i17 == true ? 1 : 0;
            } else {
                if (i11 == 0) {
                    b(i15, iArr15, iArr3);
                    b(i15, iArr4, iArr5);
                    return;
                }
                if (i11 == 1) {
                    i11--;
                    i10 = bArr[i11] & 248;
                } else {
                    i11--;
                    i10 = bArr[i11];
                }
                int i19 = i10;
                i13 = 7;
                i12 = i19 == true ? 1 : 0;
            }
            i14 = i15;
        }
    }

    public final void e(int[] iArr, int[] iArr2, int[] iArr3) {
        long[] jArr;
        long j10 = iArr2[0];
        int i10 = 0;
        while (true) {
            jArr = this.f27491j;
            if (i10 >= 10) {
                break;
            }
            jArr[i10] = ((long) iArr3[i10]) * j10;
            i10++;
        }
        for (int i11 = 1; i11 < 10; i11++) {
            long j11 = iArr2[i11];
            for (int i12 = 0; i12 < 9; i12++) {
                int i13 = i11 + i12;
                jArr[i13] = (((long) iArr3[i12]) * j11) + jArr[i13];
            }
            jArr[i11 + 9] = j11 * ((long) iArr3[9]);
        }
        long j12 = jArr[0];
        int[] iArr4 = this.k;
        iArr4[0] = ((int) j12) & 67108863;
        for (int i14 = 1; i14 < 20; i14++) {
            j12 = (j12 >> 26) + jArr[i14];
            iArr4[i14] = ((int) j12) & 67108863;
        }
        g(10, iArr, iArr4);
    }

    public final void f(int[] iArr, int[] iArr2) {
        int[] iArr3 = this.f27482a;
        e(iArr3, iArr2, iArr2);
        for (int i10 = 0; i10 < 9; i10++) {
            e(iArr3, iArr3, iArr3);
        }
        e(iArr, iArr3, iArr2);
        for (int i11 = 0; i11 < 23; i11++) {
            for (int i12 = 0; i12 < 10; i12++) {
                e(iArr3, iArr3, iArr3);
            }
            e(iArr, iArr, iArr3);
        }
        e(iArr3, iArr, iArr);
        e(iArr, iArr, iArr3);
        for (int i13 = 0; i13 < 8; i13++) {
            e(iArr3, iArr3, iArr3);
            e(iArr, iArr, iArr3);
        }
        e(iArr, iArr, iArr);
        e(iArr, iArr, iArr);
        e(iArr, iArr, iArr2);
        e(iArr, iArr, iArr);
        e(iArr, iArr, iArr);
        e(iArr, iArr, iArr2);
        e(iArr, iArr, iArr);
        e(iArr, iArr, iArr2);
    }

    public final void g(int i10, int[] iArr, int[] iArr2) {
        int i11 = iArr2[9];
        int i12 = i11 >> 21;
        iArr2[9] = i11 & 2097151;
        int i13 = 0;
        for (int i14 = 0; i14 < i10; i14++) {
            int i15 = i12 + (iArr2[i14 + 10] << 5);
            int i16 = ((i15 & 67108863) * 19) + iArr2[i14] + i13;
            iArr2[i14] = i16 & 67108863;
            i12 = i15 >> 26;
            i13 = i16 >> 26;
        }
        if (i10 < 10) {
            while (i10 < 10) {
                int i17 = i13 + iArr2[i10];
                iArr2[i10] = i17 & 67108863;
                i13 = i17 >> 26;
                i10++;
            }
        }
        int i18 = iArr2[9];
        int i19 = (i18 >> 21) * 19;
        iArr2[9] = i18 & 2097151;
        for (int i20 = 0; i20 < 10; i20++) {
            int i21 = i19 + iArr2[i20];
            iArr[i20] = i21 & 67108863;
            i19 = i21 >> 26;
        }
        h(iArr);
    }

    public final void h(int[] iArr) {
        int[] iArr2;
        int i10 = 19;
        int i11 = 0;
        while (true) {
            iArr2 = this.k;
            if (i11 >= 10) {
                break;
            }
            int i12 = i10 + iArr[i11];
            iArr2[i11] = 67108863 & i12;
            i10 = i12 >> 26;
            i11++;
        }
        int i13 = iArr2[9];
        int i14 = -((i13 >> 21) & 1);
        int i15 = ~i14;
        iArr2[9] = i13 & 2097151;
        for (int i16 = 0; i16 < 10; i16++) {
            iArr[i16] = (iArr[i16] & i15) | (iArr2[i16] & i14);
        }
    }
}
