package t3;

import dc.d1;

/* JADX INFO: loaded from: classes.dex */
public abstract class b {
    public static final long a(int i10, int i11, int i12, int i13) {
        if (!((i12 >= 0) & (i11 >= i10) & (i13 >= i12) & (i10 >= 0))) {
            j.a("maxWidth must be >= than minWidth,\nmaxHeight must be >= than minHeight,\nminWidth and minHeight must be >= 0");
        }
        return h(i10, i11, i12, i13);
    }

    public static /* synthetic */ long b(int i10, int i11, int i12, int i13, int i14) {
        if ((i14 & 1) != 0) {
            i10 = 0;
        }
        if ((i14 & 2) != 0) {
            i11 = Integer.MAX_VALUE;
        }
        if ((i14 & 4) != 0) {
            i12 = 0;
        }
        if ((i14 & 8) != 0) {
            i13 = Integer.MAX_VALUE;
        }
        return a(i10, i11, i12, i13);
    }

    public static final int c(int i10) {
        if (i10 < 8191) {
            return 13;
        }
        if (i10 < 32767) {
            return 15;
        }
        if (i10 < 65535) {
            return 16;
        }
        return i10 < 262143 ? 18 : 255;
    }

    public static final long d(long j10, long j11) {
        int i10 = (int) (j11 >> 32);
        int iJ = a.j(j10);
        int iH = a.h(j10);
        if (i10 < iJ) {
            i10 = iJ;
        }
        if (i10 <= iH) {
            iH = i10;
        }
        int i11 = (int) (j11 & 4294967295L);
        int i12 = a.i(j10);
        int iG = a.g(j10);
        if (i11 < i12) {
            i11 = i12;
        }
        if (i11 <= iG) {
            iG = i11;
        }
        return (((long) iH) << 32) | (((long) iG) & 4294967295L);
    }

    public static final long e(long j10, long j11) {
        int iJ = a.j(j10);
        int iH = a.h(j10);
        int i10 = a.i(j10);
        int iG = a.g(j10);
        int iJ2 = a.j(j11);
        if (iJ2 < iJ) {
            iJ2 = iJ;
        }
        if (iJ2 > iH) {
            iJ2 = iH;
        }
        int iH2 = a.h(j11);
        if (iH2 >= iJ) {
            iJ = iH2;
        }
        if (iJ <= iH) {
            iH = iJ;
        }
        int i11 = a.i(j11);
        if (i11 < i10) {
            i11 = i10;
        }
        if (i11 > iG) {
            i11 = iG;
        }
        int iG2 = a.g(j11);
        if (iG2 >= i10) {
            i10 = iG2;
        }
        if (i10 <= iG) {
            iG = i10;
        }
        return a(iJ2, iH, i11, iG);
    }

    public static final int f(int i10, long j10) {
        int i11 = a.i(j10);
        int iG = a.g(j10);
        if (i10 < i11) {
            i10 = i11;
        }
        return i10 > iG ? iG : i10;
    }

    public static final int g(int i10, long j10) {
        int iJ = a.j(j10);
        int iH = a.h(j10);
        if (i10 < iJ) {
            i10 = iJ;
        }
        return i10 > iH ? iH : i10;
    }

    public static final long h(int i10, int i11, int i12, int i13) {
        int i14 = i13 == Integer.MAX_VALUE ? i12 : i13;
        int iC = c(i14);
        int i15 = i11 == Integer.MAX_VALUE ? i10 : i11;
        int iC2 = c(i15);
        if (iC + iC2 > 31) {
            k(i15, i14);
        }
        int i16 = i11 + 1;
        int i17 = i13 + 1;
        int i18 = iC2 - 13;
        return (((long) (i16 & (~(i16 >> 31)))) << 33) | ((long) ((i18 >> 1) + (i18 & 1))) | (((long) i10) << 2) | (((long) i12) << (iC2 + 2)) | (((long) (i17 & (~(i17 >> 31)))) << (iC2 + 33));
    }

    public static final long i(int i10, int i11, long j10) {
        int iJ = a.j(j10) + i10;
        if (iJ < 0) {
            iJ = 0;
        }
        int iH = a.h(j10);
        if (iH != Integer.MAX_VALUE && (iH = iH + i10) < 0) {
            iH = 0;
        }
        int i12 = a.i(j10) + i11;
        if (i12 < 0) {
            i12 = 0;
        }
        int iG = a.g(j10);
        return a(iJ, iH, i12, (iG == Integer.MAX_VALUE || (iG = iG + i11) >= 0) ? iG : 0);
    }

    public static /* synthetic */ long j(int i10, int i11, long j10, int i12) {
        if ((i12 & 1) != 0) {
            i10 = 0;
        }
        if ((i12 & 2) != 0) {
            i11 = 0;
        }
        return i(i10, i11, j10);
    }

    public static final void k(int i10, int i11) {
        throw new IllegalArgumentException(h1.f.i(i10, "Can't represent a width of ", " and height of ", " in Constraints", i11));
    }

    public static final Void l(int i10) {
        throw new IllegalArgumentException(d1.f("Can't represent a size of ", i10, " in Constraints"));
    }
}
