package com.fasterxml.jackson.core.io.schubfach;

import okhttp3.internal.http.HttpStatusCodesKt;

/* JADX INFO: loaded from: classes3.dex */
public final class FloatToDecimal {
    private static final int BQ_MASK = 255;
    private static final int C_MIN = 8388608;
    static final int C_TINY = 8;
    static final int E_MAX = 39;
    static final int E_MIN = -44;
    static final int H = 9;
    static final int K_MAX = 31;
    static final int K_MIN = -45;
    private static final int MASK_28 = 268435455;
    private static final long MASK_32 = 4294967295L;
    private static final int MINUS_INF = 4;
    private static final int MINUS_ZERO = 2;
    private static final int NAN = 5;
    private static final int NON_SPECIAL = 0;
    static final int P = 24;
    private static final int PLUS_INF = 3;
    private static final int PLUS_ZERO = 1;
    static final int Q_MAX = 104;
    static final int Q_MIN = -149;
    private static final int T_MASK = 8388607;
    private static final int W = 8;
    public final int MAX_CHARS = 15;
    private final byte[] bytes = new byte[15];
    private int index;

    private FloatToDecimal() {
    }

    private void append(int i10) {
        byte[] bArr = this.bytes;
        int i11 = this.index + 1;
        this.index = i11;
        bArr[i11] = (byte) i10;
    }

    private void append8Digits(int i10) {
        int iY = y(i10);
        for (int i11 = 0; i11 < 8; i11++) {
            int i12 = iY * 10;
            appendDigit(i12 >>> 28);
            iY = i12 & MASK_28;
        }
    }

    private void appendDigit(int i10) {
        byte[] bArr = this.bytes;
        int i11 = this.index + 1;
        this.index = i11;
        bArr[i11] = (byte) (i10 + 48);
    }

    private String charsToString() {
        return new String(this.bytes, 0, 0, this.index + 1);
    }

    private void exponent(int i10) {
        append(69);
        if (i10 < 0) {
            append(45);
            i10 = -i10;
        }
        if (i10 < 10) {
            appendDigit(i10);
            return;
        }
        int i11 = (i10 * HttpStatusCodesKt.HTTP_EARLY_HINTS) >>> 10;
        appendDigit(i11);
        appendDigit(i10 - (i11 * 10));
    }

    private void removeTrailingZeroes() {
        int i10;
        byte b5;
        while (true) {
            byte[] bArr = this.bytes;
            i10 = this.index;
            b5 = bArr[i10];
            if (b5 != 48) {
                break;
            } else {
                this.index = i10 - 1;
            }
        }
        if (b5 == 46) {
            this.index = i10 + 1;
        }
    }

    private static int rop(long j10, long j11) {
        long jMultiplyHigh = MathUtils.multiplyHigh(j10, j11);
        return (int) ((((jMultiplyHigh & MASK_32) + MASK_32) >>> 32) | (jMultiplyHigh >>> 31));
    }

    private int toChars(int i10, int i11) {
        int iFlog10pow2 = MathUtils.flog10pow2(32 - Integer.numberOfLeadingZeros(i10));
        long j10 = i10;
        if (j10 >= MathUtils.pow10(iFlog10pow2)) {
            iFlog10pow2++;
        }
        int iPow10 = (int) (j10 * MathUtils.pow10(9 - iFlog10pow2));
        int i12 = i11 + iFlog10pow2;
        int i13 = (int) ((((long) iPow10) * 1441151881) >>> 57);
        int i14 = iPow10 - (100000000 * i13);
        if (i12 <= 0 || i12 > 7) {
            return (-3 >= i12 || i12 > 0) ? toChars3(i13, i14, i12) : toChars2(i13, i14, i12);
        }
        return toChars1(i13, i14, i12);
    }

    private int toChars1(int i10, int i11, int i12) {
        appendDigit(i10);
        int iY = y(i11);
        int i13 = 1;
        while (i13 < i12) {
            int i14 = iY * 10;
            appendDigit(i14 >>> 28);
            iY = i14 & MASK_28;
            i13++;
        }
        append(46);
        while (i13 <= 8) {
            int i15 = iY * 10;
            appendDigit(i15 >>> 28);
            iY = i15 & MASK_28;
            i13++;
        }
        removeTrailingZeroes();
        return 0;
    }

    private int toChars2(int i10, int i11, int i12) {
        appendDigit(0);
        append(46);
        while (i12 < 0) {
            appendDigit(0);
            i12++;
        }
        appendDigit(i10);
        append8Digits(i11);
        removeTrailingZeroes();
        return 0;
    }

    private int toChars3(int i10, int i11, int i12) {
        appendDigit(i10);
        append(46);
        append8Digits(i11);
        removeTrailingZeroes();
        exponent(i12 - 1);
        return 0;
    }

    private int toDecimal(float f10) {
        int iFloatToRawIntBits = Float.floatToRawIntBits(f10);
        int i10 = T_MASK & iFloatToRawIntBits;
        int i11 = (iFloatToRawIntBits >>> 23) & 255;
        if (i11 >= 255) {
            if (i10 != 0) {
                return 5;
            }
            return iFloatToRawIntBits > 0 ? 3 : 4;
        }
        this.index = -1;
        if (iFloatToRawIntBits < 0) {
            append(45);
        }
        if (i11 == 0) {
            if (i10 != 0) {
                return i10 < 8 ? toDecimal(Q_MIN, i10 * 10, -1) : toDecimal(Q_MIN, i10, 0);
            }
            return iFloatToRawIntBits == 0 ? 1 : 2;
        }
        int i12 = 150 - i11;
        int i13 = i10 | 8388608;
        if ((i12 > 0) & (i12 < 24)) {
            int i14 = i13 >> i12;
            if ((i14 << i12) == i13) {
                return toChars(i14, 0);
            }
        }
        return toDecimal(-i12, i13, 0);
    }

    private String toDecimalString(float f10) {
        int decimal = toDecimal(f10);
        if (decimal == 0) {
            return charsToString();
        }
        if (decimal == 1) {
            return "0.0";
        }
        if (decimal == 2) {
            return "-0.0";
        }
        if (decimal != 3) {
            return decimal != 4 ? "NaN" : "-Infinity";
        }
        return "Infinity";
    }

    public static String toString(float f10) {
        return new FloatToDecimal().toDecimalString(f10);
    }

    private int y(int i10) {
        return ((int) (MathUtils.multiplyHigh(((long) (i10 + 1)) << 28, 193428131138340668L) >>> 20)) - 1;
    }

    private int toDecimal(int i10, int i11, int i12) {
        long j10;
        int iFlog10threeQuartersPow2;
        int i13 = i11 & 1;
        long j11 = i11 << 2;
        long j12 = j11 + 2;
        if ((i11 != 8388608) | (i10 == Q_MIN)) {
            j10 = j11 - 2;
            iFlog10threeQuartersPow2 = MathUtils.flog10pow2(i10);
        } else {
            j10 = j11 - 1;
            iFlog10threeQuartersPow2 = MathUtils.flog10threeQuartersPow2(i10);
        }
        int iFlog2pow10 = i10 + MathUtils.flog2pow10(-iFlog10threeQuartersPow2) + 33;
        long jG1 = 1 + MathUtils.g1(iFlog10threeQuartersPow2);
        int iRop = rop(jG1, j11 << iFlog2pow10);
        int iRop2 = rop(jG1, j10 << iFlog2pow10);
        int iRop3 = rop(jG1, j12 << iFlog2pow10);
        int i14 = iRop >> 2;
        if (i14 >= 100) {
            int i15 = ((int) ((((long) i14) * 1717986919) >>> 34)) * 10;
            int i16 = i15 + 10;
            boolean z2 = iRop2 + i13 <= (i15 << 2);
            if (z2 != ((i16 << 2) + i13 <= iRop3)) {
                if (!z2) {
                    i15 = i16;
                }
                return toChars(i15, iFlog10threeQuartersPow2);
            }
        }
        int i17 = i14 + 1;
        boolean z10 = iRop2 + i13 <= (i14 << 2);
        if (z10 != ((i17 << 2) + i13 <= iRop3)) {
            if (!z10) {
                i14 = i17;
            }
            return toChars(i14, iFlog10threeQuartersPow2 + i12);
        }
        int i18 = iRop - ((i14 + i17) << 1);
        if (i18 >= 0 && (i18 != 0 || (i14 & 1) != 0)) {
            i14 = i17;
        }
        return toChars(i14, iFlog10threeQuartersPow2 + i12);
    }
}
