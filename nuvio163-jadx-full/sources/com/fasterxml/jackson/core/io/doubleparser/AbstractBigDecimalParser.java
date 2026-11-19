package com.fasterxml.jackson.core.io.doubleparser;

/* JADX INFO: loaded from: classes3.dex */
abstract class AbstractBigDecimalParser extends AbstractNumberParser {
    public static final int MANY_DIGITS_THRESHOLD = 32;
    protected static final int MAX_DIGITS_WITHOUT_LEADING_ZEROS = 646456993;
    protected static final long MAX_EXPONENT_NUMBER = 2147483647L;
    static final int RECURSION_THRESHOLD = 400;

    public static void checkParsedBigDecimalBounds(boolean z2, int i10, int i11, int i12, long j10) {
        if (z2 || i10 < i11) {
            throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
        }
        if (j10 <= -2147483648L || j10 > MAX_EXPONENT_NUMBER || i12 > MAX_DIGITS_WITHOUT_LEADING_ZEROS) {
            throw new NumberFormatException(AbstractNumberParser.VALUE_EXCEEDS_LIMITS);
        }
    }

    public static boolean hasManyDigits(int i10) {
        return i10 >= 32;
    }
}
