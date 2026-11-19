package com.fasterxml.jackson.core.io.doubleparser;

/* JADX INFO: loaded from: classes3.dex */
final class JavaFloatBitsFromCharArray extends AbstractJavaFloatingPointBitsFromCharArray {
    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharArray
    public long nan() {
        return Float.floatToRawIntBits(Float.NaN);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharArray
    public long negativeInfinity() {
        return Float.floatToRawIntBits(Float.NEGATIVE_INFINITY);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharArray
    public long positiveInfinity() {
        return Float.floatToRawIntBits(Float.POSITIVE_INFINITY);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharArray
    public long valueOfFloatLiteral(char[] cArr, int i10, int i11, boolean z2, long j10, int i12, boolean z10, int i13) {
        float fTryDecFloatToFloatTruncated = FastFloatMath.tryDecFloatToFloatTruncated(z2, j10, i12, z10, i13);
        return Float.isNaN(fTryDecFloatToFloatTruncated) ? Float.floatToRawIntBits(Float.parseFloat(new String(cArr, i10, i11 - i10))) : Float.floatToRawIntBits(fTryDecFloatToFloatTruncated);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharArray
    public long valueOfHexLiteral(char[] cArr, int i10, int i11, boolean z2, long j10, int i12, boolean z10, int i13) {
        float fTryHexFloatToFloatTruncated = FastFloatMath.tryHexFloatToFloatTruncated(z2, j10, i12, z10, i13);
        if (Float.isNaN(fTryHexFloatToFloatTruncated)) {
            fTryHexFloatToFloatTruncated = Float.parseFloat(new String(cArr, i10, i11 - i10));
        }
        return Float.floatToRawIntBits(fTryHexFloatToFloatTruncated);
    }
}
