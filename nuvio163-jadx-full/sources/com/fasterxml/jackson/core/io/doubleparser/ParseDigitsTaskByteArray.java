package com.fasterxml.jackson.core.io.doubleparser;

import java.math.BigInteger;
import java.util.Map;

/* JADX INFO: loaded from: classes3.dex */
class ParseDigitsTaskByteArray {
    static final /* synthetic */ boolean $assertionsDisabled = false;

    private ParseDigitsTaskByteArray() {
    }

    public static BigInteger parseDigitsIterative(byte[] bArr, int i10, int i11) {
        int i12 = i11 - i10;
        BigSignificand bigSignificand = new BigSignificand(FastIntegerMath.estimateNumBits(i12));
        int i13 = (i12 & 7) + i10;
        int iTryToParseUpTo7Digits = FastDoubleSwar.tryToParseUpTo7Digits(bArr, i10, i13);
        boolean z2 = iTryToParseUpTo7Digits >= 0;
        bigSignificand.add(iTryToParseUpTo7Digits);
        while (i13 < i11) {
            int iTryToParseEightDigits = FastDoubleSwar.tryToParseEightDigits(bArr, i13);
            z2 &= iTryToParseEightDigits >= 0;
            bigSignificand.fma(100000000, iTryToParseEightDigits);
            i13 += 8;
        }
        if (z2) {
            return bigSignificand.toBigInteger();
        }
        throw new NumberFormatException(AbstractNumberParser.SYNTAX_ERROR);
    }

    public static BigInteger parseDigitsRecursive(byte[] bArr, int i10, int i11, Map<Integer, BigInteger> map, int i12) {
        if (i11 - i10 <= i12) {
            return parseDigitsIterative(bArr, i10, i11);
        }
        int iSplitFloor16 = FastIntegerMath.splitFloor16(i10, i11);
        return parseDigitsRecursive(bArr, iSplitFloor16, i11, map, i12).add(FftMultiplier.multiply(parseDigitsRecursive(bArr, i10, iSplitFloor16, map, i12), map.get(Integer.valueOf(i11 - iSplitFloor16))));
    }
}
