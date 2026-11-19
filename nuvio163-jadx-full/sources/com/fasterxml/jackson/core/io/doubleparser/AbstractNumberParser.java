package com.fasterxml.jackson.core.io.doubleparser;

import java.util.Arrays;

/* JADX INFO: loaded from: classes3.dex */
abstract class AbstractNumberParser {
    static final byte[] CHAR_TO_HEX_MAP;
    static final byte DECIMAL_POINT_CLASS = -4;
    public static final String ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH = "offset < 0 or length > str.length";
    static final byte OTHER_CLASS = -1;
    public static final String SYNTAX_ERROR = "illegal syntax";
    public static final String VALUE_EXCEEDS_LIMITS = "value exceeds limits";

    static {
        byte[] bArr = new byte[256];
        CHAR_TO_HEX_MAP = bArr;
        Arrays.fill(bArr, (byte) -1);
        for (char c5 = '0'; c5 <= '9'; c5 = (char) (c5 + 1)) {
            CHAR_TO_HEX_MAP[c5] = (byte) (c5 - '0');
        }
        for (char c10 = 'A'; c10 <= 'F'; c10 = (char) (c10 + 1)) {
            CHAR_TO_HEX_MAP[c10] = (byte) (c10 - '7');
        }
        for (char c11 = 'a'; c11 <= 'f'; c11 = (char) (c11 + 1)) {
            CHAR_TO_HEX_MAP[c11] = (byte) (c11 - 'W');
        }
        CHAR_TO_HEX_MAP[46] = DECIMAL_POINT_CLASS;
    }

    public static byte charAt(byte[] bArr, int i10, int i11) {
        if (i10 < i11) {
            return bArr[i10];
        }
        return (byte) 0;
    }

    public static int checkBounds(int i10, int i11, int i12, int i13) {
        if (i12 <= i13) {
            return checkBounds(i10, i11, i12);
        }
        throw new NumberFormatException(VALUE_EXCEEDS_LIMITS);
    }

    public static int lookupHex(byte b5) {
        return CHAR_TO_HEX_MAP[b5 & 255];
    }

    public static char charAt(char[] cArr, int i10, int i11) {
        if (i10 < i11) {
            return cArr[i10];
        }
        return (char) 0;
    }

    public static int lookupHex(char c5) {
        if (c5 < 128) {
            return CHAR_TO_HEX_MAP[c5];
        }
        return -1;
    }

    public static char charAt(CharSequence charSequence, int i10, int i11) {
        if (i10 < i11) {
            return charSequence.charAt(i10);
        }
        return (char) 0;
    }

    public static int checkBounds(int i10, int i11, int i12) {
        if ((((i10 - i12) - i11) | i11 | i12) >= 0) {
            return i12 + i11;
        }
        throw new IllegalArgumentException(ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH);
    }
}
