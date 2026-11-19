package com.fasterxml.jackson.core.io.doubleparser;

/* JADX INFO: loaded from: classes3.dex */
class FastFloatMath {
    private static final int FLOAT_EXPONENT_BIAS = 127;
    private static final int FLOAT_MAX_EXPONENT_POWER_OF_TEN = 38;
    private static final int FLOAT_MAX_EXPONENT_POWER_OF_TWO = 127;
    private static final int FLOAT_MIN_EXPONENT_POWER_OF_TEN = -45;
    private static final int FLOAT_MIN_EXPONENT_POWER_OF_TWO = -126;
    private static final float[] FLOAT_POWER_OF_TEN = {1.0f, 10.0f, 100.0f, 1000.0f, 10000.0f, 100000.0f, 1000000.0f, 1.0E7f, 1.0E8f, 1.0E9f, 1.0E10f};
    private static final int FLOAT_SIGNIFICAND_WIDTH = 24;

    private FastFloatMath() {
    }

    public static float fastScalb(float f10, int i10) {
        return Float.intBitsToFloat((i10 + 127) << 23) * f10;
    }

    public static float tryDecFloatToFloatTruncated(boolean z2, long j10, int i10, boolean z10, int i11) {
        if (j10 == 0) {
            return z2 ? -0.0f : 0.0f;
        }
        if (!z10) {
            if (FLOAT_MIN_EXPONENT_POWER_OF_TEN > i10 || i10 > 38) {
                return Float.NaN;
            }
            return tryDecToFloatWithFastAlgorithm(z2, j10, i10);
        }
        if (FLOAT_MIN_EXPONENT_POWER_OF_TEN <= i11 && i11 <= 38) {
            float fTryDecToFloatWithFastAlgorithm = tryDecToFloatWithFastAlgorithm(z2, j10, i11);
            float fTryDecToFloatWithFastAlgorithm2 = tryDecToFloatWithFastAlgorithm(z2, j10 + 1, i11);
            if (!Float.isNaN(fTryDecToFloatWithFastAlgorithm) && fTryDecToFloatWithFastAlgorithm2 == fTryDecToFloatWithFastAlgorithm) {
                return fTryDecToFloatWithFastAlgorithm;
            }
        }
        return Float.NaN;
    }

    public static float tryDecToFloatWithFastAlgorithm(boolean z2, long j10, int i10) {
        if (-10 <= i10 && i10 <= 10 && Long.compare(Long.MIN_VALUE ^ j10, -9223372036837998593L) <= 0) {
            float f10 = j10;
            float f11 = i10 < 0 ? f10 / FLOAT_POWER_OF_TEN[-i10] : f10 * FLOAT_POWER_OF_TEN[i10];
            return z2 ? -f11 : f11;
        }
        long j11 = FastDoubleMath.MANTISSA_64[i10 + 325];
        long j12 = ((((long) i10) * 217706) >> 16) + 191;
        int iNumberOfLeadingZeros = Long.numberOfLeadingZeros(j10);
        long jUnsignedMultiplyHigh = FastIntegerMath.unsignedMultiplyHigh(j10 << iNumberOfLeadingZeros, j11);
        long j13 = jUnsignedMultiplyHigh >>> 63;
        long j14 = jUnsignedMultiplyHigh >>> ((int) (38 + j13));
        int i11 = iNumberOfLeadingZeros + ((int) (j13 ^ 1));
        long j15 = jUnsignedMultiplyHigh & 274877906943L;
        if (j15 != 274877906943L) {
            if (j15 != 0 || (3 & j14) != 1) {
                long j16 = (j14 + 1) >>> 1;
                if (j16 >= 16777216) {
                    i11--;
                    j16 = 8388608;
                }
                long j17 = j16 & (-8388609);
                long j18 = j12 - ((long) i11);
                if (j18 >= 1 && j18 <= 254) {
                    return Float.intBitsToFloat((int) (j17 | (j18 << 23) | (z2 ? 2147483648L : 0L)));
                }
            }
        }
        return Float.NaN;
    }

    public static float tryHexFloatToFloatTruncated(boolean z2, long j10, int i10, boolean z10, int i11) {
        if (z10) {
            i10 = i11;
        }
        if (FLOAT_MIN_EXPONENT_POWER_OF_TWO > i10 || i10 > 127) {
            return Float.NaN;
        }
        float fFastScalb = fastScalb(j10 + (j10 < 0 ? 1.8446744E19f : 0.0f), i10);
        return z2 ? -fFastScalb : fFastScalb;
    }
}
