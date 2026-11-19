package com.fasterxml.jackson.core.io.doubleparser;

import androidx.media3.exoplayer.Renderer;

/* JADX INFO: loaded from: classes3.dex */
abstract class AbstractJavaFloatingPointBitsFromByteArray extends AbstractFloatValueParser {
    private long parseDecFloatLiteral(byte[] bArr, int i10, int i11, int i12, boolean z2, boolean z10) {
        int i13;
        int i14;
        int i15;
        int i16;
        long j10;
        boolean z11;
        int i17;
        int iTryToParseFourDigits;
        int i18 = -1;
        int i19 = i10;
        long j11 = 0;
        byte b5 = 0;
        boolean z12 = false;
        while (true) {
            if (i19 >= i12) {
                break;
            }
            b5 = bArr[i19];
            if (!FastDoubleSwar.isDigit(b5)) {
                if (b5 != 46) {
                    break;
                }
                z12 |= i18 >= 0;
                int i20 = i19;
                while (i20 < i12 - 4 && (iTryToParseFourDigits = FastDoubleSwar.tryToParseFourDigits(bArr, i20 + 1)) >= 0) {
                    j11 = (j11 * Renderer.DEFAULT_DURATION_TO_PROGRESS_US) + ((long) iTryToParseFourDigits);
                    i20 += 4;
                }
                int i21 = i19;
                i19 = i20;
                i18 = i21;
            } else {
                j11 = ((j11 * 10) + ((long) b5)) - 48;
            }
            i19++;
        }
        if (i18 < 0) {
            i14 = i19 - i10;
            i18 = i19;
            i13 = 0;
        } else {
            i13 = (i18 - i19) + 1;
            i14 = (i19 - i10) - 1;
        }
        if ((b5 | 32) == 101) {
            i15 = i19 + 1;
            byte bCharAt = AbstractNumberParser.charAt(bArr, i15, i12);
            boolean z13 = bCharAt == 45;
            if (z13 || bCharAt == 43) {
                i15 = i19 + 2;
                bCharAt = AbstractNumberParser.charAt(bArr, i15, i12);
            }
            boolean z14 = z12 | (!FastDoubleSwar.isDigit(bCharAt));
            int i22 = 0;
            do {
                if (i22 < 1024) {
                    i22 = ((i22 * 10) + bCharAt) - 48;
                }
                i15++;
                bCharAt = AbstractNumberParser.charAt(bArr, i15, i12);
            } while (FastDoubleSwar.isDigit(bCharAt));
            if (z13) {
                i22 = -i22;
            }
            i13 += i22;
            int i23 = i22;
            b5 = bCharAt;
            i16 = i23;
            z12 = z14;
        } else {
            i15 = i19;
            i16 = 0;
        }
        if ((b5 | 34) == 102) {
            i15++;
        }
        int iSkipWhitespace = skipWhitespace(bArr, i15, i12);
        if (z12 || iSkipWhitespace < i12 || (!z10 && i14 == 0)) {
            throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
        }
        if (i14 > 19) {
            int i24 = i10;
            int i25 = 0;
            long j12 = 0;
            while (i24 < i19) {
                byte b10 = bArr[i24];
                if (b10 != 46) {
                    if (Long.compare(j12 ^ Long.MIN_VALUE, -8223372036854775808L) >= 0) {
                        break;
                    }
                    j12 = ((j12 * 10) + ((long) b10)) - 48;
                } else {
                    i25++;
                }
                i24++;
            }
            i17 = (i18 - i24) + i25 + i16;
            z11 = i24 < i19;
            j10 = j12;
        } else {
            j10 = j11;
            z11 = false;
            i17 = 0;
        }
        return valueOfFloatLiteral(bArr, i11, i12, z2, j10, i13, z11, i17);
    }

    private long parseHexFloatingPointLiteral(byte[] bArr, int i10, int i11, int i12, boolean z2) {
        int i13;
        int iMin;
        int i14;
        int i15;
        long j10;
        int i16;
        boolean z10;
        int i17 = -1;
        int i18 = i10;
        long j11 = 0;
        byte b5 = 0;
        boolean z11 = false;
        while (true) {
            if (i18 >= i12) {
                break;
            }
            b5 = bArr[i18];
            int iLookupHex = AbstractNumberParser.lookupHex(b5);
            if (iLookupHex < 0) {
                if (iLookupHex != -4) {
                    break;
                }
                z11 |= i17 >= 0;
                i17 = i18;
            } else {
                j11 = (j11 << 4) | ((long) iLookupHex);
            }
            i18++;
        }
        if (i17 < 0) {
            i13 = i18 - i10;
            i17 = i18;
            iMin = 0;
        } else {
            i13 = (i18 - i10) - 1;
            iMin = Math.min((i17 - i18) + 1, 1024) * 4;
        }
        boolean z12 = (b5 | 32) == 112;
        if (z12) {
            i14 = i18 + 1;
            byte bCharAt = AbstractNumberParser.charAt(bArr, i14, i12);
            boolean z13 = bCharAt == 45;
            if (z13 || bCharAt == 43) {
                i14 = i18 + 2;
                bCharAt = AbstractNumberParser.charAt(bArr, i14, i12);
            }
            boolean z14 = z11 | (!FastDoubleSwar.isDigit(bCharAt));
            int i19 = 0;
            do {
                if (i19 < 1024) {
                    i19 = ((i19 * 10) + bCharAt) - 48;
                }
                i14++;
                bCharAt = AbstractNumberParser.charAt(bArr, i14, i12);
            } while (FastDoubleSwar.isDigit(bCharAt));
            if (z13) {
                i19 = -i19;
            }
            iMin += i19;
            int i20 = i19;
            b5 = bCharAt;
            i15 = i20;
            z11 = z14;
        } else {
            i14 = i18;
            i15 = 0;
        }
        if ((b5 | 34) == 102) {
            i14++;
        }
        int iSkipWhitespace = skipWhitespace(bArr, i14, i12);
        if (z11 || iSkipWhitespace < i12 || i13 == 0 || !z12) {
            throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
        }
        if (i13 > 16) {
            int i21 = i10;
            int i22 = 0;
            long j12 = 0;
            while (i21 < i18) {
                int iLookupHex2 = AbstractNumberParser.lookupHex(bArr[i21]);
                if (iLookupHex2 < 0) {
                    i22++;
                } else {
                    if (Long.compare(j12 ^ Long.MIN_VALUE, -8223372036854775808L) >= 0) {
                        break;
                    }
                    j12 = (j12 << 4) | ((long) iLookupHex2);
                }
                i21++;
            }
            boolean z15 = i21 < i18;
            int i23 = i22;
            iSkipWhitespace = i21;
            i16 = i23;
            z10 = z15;
            j10 = j12;
        } else {
            j10 = j11;
            i16 = 0;
            z10 = false;
        }
        return valueOfHexLiteral(bArr, i11, i12, z2, j10, iMin, z10, (((i17 - iSkipWhitespace) + i16) * 4) + i15);
    }

    private long parseNaNOrInfinity(byte[] bArr, int i10, int i11, boolean z2) {
        if (bArr[i10] == 78) {
            int i12 = i10 + 2;
            if (i12 < i11 && bArr[i10 + 1] == 97 && bArr[i12] == 78 && skipWhitespace(bArr, i10 + 3, i11) == i11) {
                return nan();
            }
        } else if (i10 + 7 < i11 && FastDoubleSwar.readLongLE(bArr, i10) == 8751735898823355977L && skipWhitespace(bArr, i10 + 8, i11) == i11) {
            return z2 ? negativeInfinity() : positiveInfinity();
        }
        throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
    }

    private static int skipWhitespace(byte[] bArr, int i10, int i11) {
        while (i10 < i11 && (bArr[i10] & 255) <= 32) {
            i10++;
        }
        return i10;
    }

    public abstract long nan();

    public abstract long negativeInfinity();

    public long parseFloatingPointLiteral(byte[] bArr, int i10, int i11) {
        int i12;
        int iCheckBounds = AbstractNumberParser.checkBounds(bArr.length, i10, i11);
        int iSkipWhitespace = skipWhitespace(bArr, i10, iCheckBounds);
        if (iSkipWhitespace == iCheckBounds) {
            throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
        }
        byte bCharAt = bArr[iSkipWhitespace];
        boolean z2 = bCharAt == 45;
        if ((z2 || bCharAt == 43) && (bCharAt = AbstractNumberParser.charAt(bArr, (iSkipWhitespace = iSkipWhitespace + 1), iCheckBounds)) == 0) {
            throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
        }
        if (bCharAt >= 73) {
            return parseNaNOrInfinity(bArr, iSkipWhitespace, iCheckBounds, z2);
        }
        boolean z10 = bCharAt == 48;
        if (z10) {
            int i13 = iSkipWhitespace + 1;
            if ((AbstractNumberParser.charAt(bArr, i13, iCheckBounds) | 32) == 120) {
                return parseHexFloatingPointLiteral(bArr, iSkipWhitespace + 2, i10, iCheckBounds, z2);
            }
            i12 = i13;
        } else {
            i12 = iSkipWhitespace;
        }
        return parseDecFloatLiteral(bArr, i12, i10, iCheckBounds, z2, z10);
    }

    public abstract long positiveInfinity();

    public abstract long valueOfFloatLiteral(byte[] bArr, int i10, int i11, boolean z2, long j10, int i12, boolean z10, int i13);

    public abstract long valueOfHexLiteral(byte[] bArr, int i10, int i11, boolean z2, long j10, int i12, boolean z10, int i13);
}
