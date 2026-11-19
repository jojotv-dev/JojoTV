package com.fasterxml.jackson.core.io.doubleparser;

import androidx.media3.exoplayer.Renderer;

/* JADX INFO: loaded from: classes3.dex */
abstract class AbstractJavaFloatingPointBitsFromCharArray extends AbstractFloatValueParser {
    private static final boolean CONDITIONAL_COMPILATION_PARSE_EIGHT_HEX_DIGITS = true;

    private long parseDecFloatLiteral(char[] cArr, int i10, int i11, int i12, boolean z2, boolean z10) {
        int i13;
        int i14;
        int i15;
        int i16;
        long j10;
        boolean z11;
        int i17;
        int iMin = Math.min(i12 - 4, 1073741824);
        int i18 = -1;
        int i19 = i10;
        long j11 = 0;
        char c5 = 0;
        boolean z12 = false;
        while (true) {
            if (i19 >= i12) {
                break;
            }
            c5 = cArr[i19];
            if (!FastDoubleSwar.isDigit(c5)) {
                if (c5 != '.') {
                    break;
                }
                z12 |= i18 >= 0;
                int i20 = i19;
                while (i20 < iMin) {
                    int iTryToParseFourDigits = FastDoubleSwar.tryToParseFourDigits(cArr, i20 + 1);
                    if (iTryToParseFourDigits < 0) {
                        break;
                    }
                    j11 = (j11 * Renderer.DEFAULT_DURATION_TO_PROGRESS_US) + ((long) iTryToParseFourDigits);
                    i20 += 4;
                }
                int i21 = i19;
                i19 = i20;
                i18 = i21;
            } else {
                j11 = ((j11 * 10) + ((long) c5)) - 48;
            }
            i19++;
        }
        if (i18 < 0) {
            i13 = i19 - i10;
            i18 = i19;
            i14 = 0;
        } else {
            i13 = (i19 - i10) - 1;
            i14 = (i18 - i19) + 1;
        }
        if ((c5 | ' ') == 101) {
            i15 = i19 + 1;
            char cCharAt = AbstractNumberParser.charAt(cArr, i15, i12);
            boolean z13 = cCharAt == '-';
            if (z13 || cCharAt == '+') {
                i15 = i19 + 2;
                cCharAt = AbstractNumberParser.charAt(cArr, i15, i12);
            }
            boolean z14 = z12 | (!FastDoubleSwar.isDigit(cCharAt));
            int i22 = 0;
            do {
                if (i22 < 1024) {
                    i22 = ((i22 * 10) + cCharAt) - 48;
                }
                i15++;
                cCharAt = AbstractNumberParser.charAt(cArr, i15, i12);
            } while (FastDoubleSwar.isDigit(cCharAt));
            if (z13) {
                i22 = -i22;
            }
            i14 += i22;
            int i23 = i22;
            c5 = cCharAt;
            i16 = i23;
            z12 = z14;
        } else {
            i15 = i19;
            i16 = 0;
        }
        if ((c5 | '\"') == 102) {
            i15++;
        }
        int iSkipWhitespace = skipWhitespace(cArr, i15, i12);
        if (z12 || iSkipWhitespace < i12 || (!z10 && i13 == 0)) {
            throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
        }
        if (i13 > 19) {
            int i24 = i10;
            int i25 = 0;
            long j12 = 0;
            while (i24 < i19) {
                char c10 = cArr[i24];
                if (c10 != '.') {
                    if (Long.compare(j12 ^ Long.MIN_VALUE, -8223372036854775808L) >= 0) {
                        break;
                    }
                    j12 = ((j12 * 10) + ((long) c10)) - 48;
                } else {
                    i25++;
                }
                i24++;
            }
            i17 = i16 + (i18 - i24) + i25;
            z11 = i24 < i19;
            j10 = j12;
        } else {
            j10 = j11;
            z11 = false;
            i17 = 0;
        }
        return valueOfFloatLiteral(cArr, i11, i12, z2, j10, i14, z11, i17);
    }

    private long parseHexFloatLiteral(char[] cArr, int i10, int i11, int i12, boolean z2) {
        int i13;
        int iMin;
        int i14;
        int i15;
        long j10;
        boolean z10;
        int i16;
        int i17 = -1;
        int i18 = i10;
        long j11 = 0;
        char cCharAt = 0;
        boolean z11 = false;
        while (i18 < i12) {
            cCharAt = cArr[i18];
            int iLookupHex = AbstractNumberParser.lookupHex(cCharAt);
            if (iLookupHex < 0) {
                if (iLookupHex != -4) {
                    break;
                }
                z11 |= i17 >= 0;
                int i19 = i18;
                while (i19 < i12 - 8) {
                    long jTryToParseEightHexDigits = tryToParseEightHexDigits(cArr, i19 + 1);
                    if (jTryToParseEightHexDigits < 0) {
                        break;
                    }
                    j11 = (j11 << 32) + jTryToParseEightHexDigits;
                    i19 += 8;
                }
                int i20 = i18;
                i18 = i19;
                i17 = i20;
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
        boolean z12 = (cCharAt | ' ') == 112;
        if (z12) {
            i14 = i18 + 1;
            cCharAt = AbstractNumberParser.charAt(cArr, i14, i12);
            boolean z13 = cCharAt == '-';
            if (z13 || cCharAt == '+') {
                i14 = i18 + 2;
                cCharAt = AbstractNumberParser.charAt(cArr, i14, i12);
            }
            boolean z14 = (!FastDoubleSwar.isDigit(cCharAt)) | z11;
            int i21 = 0;
            do {
                if (i21 < 1024) {
                    i21 = ((i21 * 10) + cCharAt) - 48;
                }
                i14++;
                cCharAt = AbstractNumberParser.charAt(cArr, i14, i12);
            } while (FastDoubleSwar.isDigit(cCharAt));
            if (z13) {
                i21 = -i21;
            }
            iMin += i21;
            i15 = i21;
            z11 = z14;
        } else {
            i14 = i18;
            i15 = 0;
        }
        if ((cCharAt | '\"') == 102) {
            i14++;
        }
        int iSkipWhitespace = skipWhitespace(cArr, i14, i12);
        if (z11 || iSkipWhitespace < i12 || i13 == 0 || !z12) {
            throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
        }
        if (i13 > 16) {
            int i22 = i10;
            int i23 = 0;
            j10 = 0;
            while (i22 < i18) {
                int iLookupHex2 = AbstractNumberParser.lookupHex(cArr[i22]);
                if (iLookupHex2 < 0) {
                    i23++;
                } else {
                    if (Long.compare(j10 ^ Long.MIN_VALUE, -8223372036854775808L) >= 0) {
                        break;
                    }
                    j10 = (j10 << 4) | ((long) iLookupHex2);
                }
                i22++;
            }
            z10 = i22 < i18;
            i16 = i23;
            iSkipWhitespace = i22;
        } else {
            j10 = j11;
            z10 = false;
            i16 = 0;
        }
        return valueOfHexLiteral(cArr, i11, i12, z2, j10, iMin, z10, (((i17 - iSkipWhitespace) + i16) * 4) + i15);
    }

    private long parseNaNOrInfinity(char[] cArr, int i10, int i11, boolean z2) {
        char c5 = cArr[i10];
        if (c5 == 'N') {
            int i12 = i10 + 2;
            if (i12 < i11 && cArr[i10 + 1] == 'a' && cArr[i12] == 'N' && skipWhitespace(cArr, i10 + 3, i11) == i11) {
                return nan();
            }
        } else {
            int i13 = i10 + 7;
            if (i13 < i11 && c5 == 'I' && cArr[i10 + 1] == 'n' && cArr[i10 + 2] == 'f' && cArr[i10 + 3] == 'i' && cArr[i10 + 4] == 'n' && cArr[i10 + 5] == 'i' && cArr[i10 + 6] == 't' && cArr[i13] == 'y' && skipWhitespace(cArr, i10 + 8, i11) == i11) {
                return z2 ? negativeInfinity() : positiveInfinity();
            }
        }
        throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
    }

    private static int skipWhitespace(char[] cArr, int i10, int i11) {
        while (i10 < i11 && cArr[i10] <= ' ') {
            i10++;
        }
        return i10;
    }

    private long tryToParseEightHexDigits(char[] cArr, int i10) {
        return FastDoubleSwar.tryToParseEightHexDigits(cArr, i10);
    }

    public abstract long nan();

    public abstract long negativeInfinity();

    public long parseFloatingPointLiteral(char[] cArr, int i10, int i11) {
        int i12;
        int iCheckBounds = AbstractNumberParser.checkBounds(cArr.length, i10, i11);
        int iSkipWhitespace = skipWhitespace(cArr, i10, iCheckBounds);
        if (iSkipWhitespace == iCheckBounds) {
            throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
        }
        char cCharAt = cArr[iSkipWhitespace];
        boolean z2 = cCharAt == '-';
        if ((z2 || cCharAt == '+') && (cCharAt = AbstractNumberParser.charAt(cArr, (iSkipWhitespace = iSkipWhitespace + 1), iCheckBounds)) == 0) {
            throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
        }
        if (cCharAt >= 'I') {
            return parseNaNOrInfinity(cArr, iSkipWhitespace, iCheckBounds, z2);
        }
        boolean z10 = cCharAt == '0';
        if (z10) {
            int i13 = iSkipWhitespace + 1;
            if ((AbstractNumberParser.charAt(cArr, i13, iCheckBounds) | ' ') == 120) {
                return parseHexFloatLiteral(cArr, iSkipWhitespace + 2, i10, iCheckBounds, z2);
            }
            i12 = i13;
        } else {
            i12 = iSkipWhitespace;
        }
        return parseDecFloatLiteral(cArr, i12, i10, iCheckBounds, z2, z10);
    }

    public abstract long positiveInfinity();

    public abstract long valueOfFloatLiteral(char[] cArr, int i10, int i11, boolean z2, long j10, int i12, boolean z10, int i13);

    public abstract long valueOfHexLiteral(char[] cArr, int i10, int i11, boolean z2, long j10, int i12, boolean z10, int i13);
}
