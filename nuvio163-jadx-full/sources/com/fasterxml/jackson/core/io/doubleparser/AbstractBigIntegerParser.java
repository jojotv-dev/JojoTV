package com.fasterxml.jackson.core.io.doubleparser;

/* JADX INFO: loaded from: classes3.dex */
abstract class AbstractBigIntegerParser extends AbstractNumberParser {
    private static final int MAX_DECIMAL_DIGITS = 646456993;
    private static final int MAX_HEX_DIGITS = 536870912;
    static final int RECURSION_THRESHOLD = 400;

    public static void checkDecBigIntegerBounds(int i10) {
        if (i10 > MAX_DECIMAL_DIGITS) {
            throw new NumberFormatException(AbstractNumberParser.VALUE_EXCEEDS_LIMITS);
        }
    }

    public static void checkHexBigIntegerBounds(int i10) {
        if (i10 > 536870912) {
            throw new NumberFormatException(AbstractNumberParser.VALUE_EXCEEDS_LIMITS);
        }
    }

    public static boolean hasManyDigits(int i10) {
        return i10 > 18;
    }
}
