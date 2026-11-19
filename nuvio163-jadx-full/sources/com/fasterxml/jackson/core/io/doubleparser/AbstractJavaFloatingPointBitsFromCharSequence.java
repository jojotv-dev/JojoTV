package com.fasterxml.jackson.core.io.doubleparser;

/* JADX INFO: loaded from: classes3.dex */
abstract class AbstractJavaFloatingPointBitsFromCharSequence extends AbstractFloatValueParser {
    private long parseDecFloatLiteral(CharSequence charSequence, int i10, int i11, int i12, boolean z2, boolean z10) {
        int i13;
        int i14;
        int i15;
        int i16;
        long j10;
        boolean z11;
        int i17;
        int i18 = -1;
        int i19 = i10;
        long j11 = 0;
        char cCharAt = 0;
        boolean z12 = false;
        while (true) {
            if (i19 >= i12) {
                break;
            }
            cCharAt = charSequence.charAt(i19);
            if (!FastDoubleSwar.isDigit(cCharAt)) {
                if (cCharAt != '.') {
                    break;
                }
                z12 |= i18 >= 0;
                i18 = i19;
            } else {
                j11 = ((j11 * 10) + ((long) cCharAt)) - 48;
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
        if ((cCharAt | ' ') == 101) {
            i15 = i19 + 1;
            char cCharAt2 = AbstractNumberParser.charAt(charSequence, i15, i12);
            boolean z13 = cCharAt2 == '-';
            if (z13 || cCharAt2 == '+') {
                i15 = i19 + 2;
                cCharAt2 = AbstractNumberParser.charAt(charSequence, i15, i12);
            }
            boolean z14 = z12 | (!FastDoubleSwar.isDigit(cCharAt2));
            int i20 = 0;
            do {
                if (i20 < 1024) {
                    i20 = ((i20 * 10) + cCharAt2) - 48;
                }
                i15++;
                cCharAt2 = AbstractNumberParser.charAt(charSequence, i15, i12);
            } while (FastDoubleSwar.isDigit(cCharAt2));
            if (z13) {
                i20 = -i20;
            }
            i13 += i20;
            int i21 = i20;
            cCharAt = cCharAt2;
            i16 = i21;
            z12 = z14;
        } else {
            i15 = i19;
            i16 = 0;
        }
        if ((cCharAt | '\"') == 102) {
            i15++;
        }
        int iSkipWhitespace = skipWhitespace(charSequence, i15, i12);
        if (z12 || iSkipWhitespace < i12 || (!z10 && i14 == 0)) {
            throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
        }
        if (i14 > 19) {
            int i22 = i10;
            int i23 = 0;
            long j12 = 0;
            while (i22 < i19) {
                char cCharAt3 = charSequence.charAt(i22);
                if (cCharAt3 != '.') {
                    if (Long.compare(j12 ^ Long.MIN_VALUE, -8223372036854775808L) >= 0) {
                        break;
                    }
                    j12 = ((j12 * 10) + ((long) cCharAt3)) - 48;
                } else {
                    i23++;
                }
                i22++;
            }
            i17 = (i18 - i22) + i23 + i16;
            z11 = i22 < i19;
            j10 = j12;
        } else {
            j10 = j11;
            z11 = false;
            i17 = 0;
        }
        return valueOfFloatLiteral(charSequence, i11, i12, z2, j10, i13, z11, i17);
    }

    private long parseHexFloatLiteral(CharSequence charSequence, int i10, int i11, int i12, boolean z2) {
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
        char cCharAt = 0;
        boolean z11 = false;
        while (true) {
            if (i18 >= i12) {
                break;
            }
            cCharAt = charSequence.charAt(i18);
            int iLookupHex = AbstractNumberParser.lookupHex(cCharAt);
            if (iLookupHex < 0) {
                if (iLookupHex != -4) {
                    break;
                }
                z11 |= i17 >= 0;
                int i19 = i18;
                while (i19 < i12 - 8) {
                    long jTryToParseEightHexDigits = FastDoubleSwar.tryToParseEightHexDigits(charSequence, i19 + 1);
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
            char cCharAt2 = AbstractNumberParser.charAt(charSequence, i14, i12);
            boolean z13 = cCharAt2 == '-';
            if (z13 || cCharAt2 == '+') {
                i14 = i18 + 2;
                cCharAt2 = AbstractNumberParser.charAt(charSequence, i14, i12);
            }
            boolean z14 = z11 | (!FastDoubleSwar.isDigit(cCharAt2));
            int i21 = 0;
            do {
                if (i21 < 1024) {
                    i21 = ((i21 * 10) + cCharAt2) - 48;
                }
                i14++;
                cCharAt2 = AbstractNumberParser.charAt(charSequence, i14, i12);
            } while (FastDoubleSwar.isDigit(cCharAt2));
            if (z13) {
                i21 = -i21;
            }
            iMin += i21;
            int i22 = i21;
            cCharAt = cCharAt2;
            i15 = i22;
            z11 = z14;
        } else {
            i14 = i18;
            i15 = 0;
        }
        if ((cCharAt | '\"') == 102) {
            i14++;
        }
        int iSkipWhitespace = skipWhitespace(charSequence, i14, i12);
        if (z11 || iSkipWhitespace < i12 || i13 == 0 || !z12) {
            throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
        }
        if (i13 > 16) {
            int i23 = i10;
            int i24 = 0;
            long j12 = 0;
            while (i23 < i18) {
                int iLookupHex2 = AbstractNumberParser.lookupHex(charSequence.charAt(i23));
                if (iLookupHex2 < 0) {
                    i24++;
                } else {
                    if (Long.compare(j12 ^ Long.MIN_VALUE, -8223372036854775808L) >= 0) {
                        break;
                    }
                    j12 = (j12 << 4) | ((long) iLookupHex2);
                }
                i23++;
            }
            boolean z15 = i23 < i18;
            int i25 = i24;
            iSkipWhitespace = i23;
            i16 = i25;
            z10 = z15;
            j10 = j12;
        } else {
            j10 = j11;
            i16 = 0;
            z10 = false;
        }
        return valueOfHexLiteral(charSequence, i11, i12, z2, j10, iMin, z10, (((i17 - iSkipWhitespace) + i16) * 4) + i15);
    }

    private long parseNaNOrInfinity(CharSequence charSequence, int i10, int i11, boolean z2) {
        if (charSequence.charAt(i10) == 'N') {
            int i12 = i10 + 2;
            if (i12 < i11 && charSequence.charAt(i10 + 1) == 'a' && charSequence.charAt(i12) == 'N' && skipWhitespace(charSequence, i10 + 3, i11) == i11) {
                return nan();
            }
        } else {
            int i13 = i10 + 7;
            if (i13 < i11 && charSequence.charAt(i10) == 'I' && charSequence.charAt(i10 + 1) == 'n' && charSequence.charAt(i10 + 2) == 'f' && charSequence.charAt(i10 + 3) == 'i' && charSequence.charAt(i10 + 4) == 'n' && charSequence.charAt(i10 + 5) == 'i' && charSequence.charAt(i10 + 6) == 't' && charSequence.charAt(i13) == 'y' && skipWhitespace(charSequence, i10 + 8, i11) == i11) {
                return z2 ? negativeInfinity() : positiveInfinity();
            }
        }
        throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
    }

    private static int skipWhitespace(CharSequence charSequence, int i10, int i11) {
        while (i10 < i11 && charSequence.charAt(i10) <= ' ') {
            i10++;
        }
        return i10;
    }

    public abstract long nan();

    public abstract long negativeInfinity();

    public final long parseFloatingPointLiteral(CharSequence charSequence, int i10, int i11) {
        int i12;
        int iCheckBounds = AbstractNumberParser.checkBounds(charSequence.length(), i10, i11);
        int iSkipWhitespace = skipWhitespace(charSequence, i10, iCheckBounds);
        if (iSkipWhitespace == iCheckBounds) {
            throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
        }
        char cCharAt = charSequence.charAt(iSkipWhitespace);
        boolean z2 = cCharAt == '-';
        if ((z2 || cCharAt == '+') && (cCharAt = AbstractNumberParser.charAt(charSequence, (iSkipWhitespace = iSkipWhitespace + 1), iCheckBounds)) == 0) {
            throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
        }
        if (cCharAt >= 'I') {
            return parseNaNOrInfinity(charSequence, iSkipWhitespace, iCheckBounds, z2);
        }
        boolean z10 = cCharAt == '0';
        if (z10) {
            int i13 = iSkipWhitespace + 1;
            if ((AbstractNumberParser.charAt(charSequence, i13, iCheckBounds) | ' ') == 120) {
                return parseHexFloatLiteral(charSequence, iSkipWhitespace + 2, i10, iCheckBounds, z2);
            }
            i12 = i13;
        } else {
            i12 = iSkipWhitespace;
        }
        return parseDecFloatLiteral(charSequence, i12, i10, iCheckBounds, z2, z10);
    }

    public abstract long positiveInfinity();

    public abstract long valueOfFloatLiteral(CharSequence charSequence, int i10, int i11, boolean z2, long j10, int i12, boolean z10, int i13);

    public abstract long valueOfHexLiteral(CharSequence charSequence, int i10, int i11, boolean z2, long j10, int i12, boolean z10, int i13);
}
