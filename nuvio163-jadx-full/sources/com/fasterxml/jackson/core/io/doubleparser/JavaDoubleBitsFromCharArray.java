package com.fasterxml.jackson.core.io.doubleparser;

/* JADX INFO: loaded from: classes3.dex */
final class JavaDoubleBitsFromCharArray extends AbstractJavaFloatingPointBitsFromCharArray {
    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharArray
    public long nan() {
        return Double.doubleToRawLongBits(Double.NaN);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharArray
    public long negativeInfinity() {
        return Double.doubleToRawLongBits(Double.NEGATIVE_INFINITY);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharArray
    public long positiveInfinity() {
        return Double.doubleToRawLongBits(Double.POSITIVE_INFINITY);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharArray
    public long valueOfFloatLiteral(char[] cArr, int i10, int i11, boolean z2, long j10, int i12, boolean z10, int i13) {
        double dTryDecFloatToDoubleTruncated = FastDoubleMath.tryDecFloatToDoubleTruncated(z2, j10, i12, z10, i13);
        if (Double.isNaN(dTryDecFloatToDoubleTruncated)) {
            dTryDecFloatToDoubleTruncated = Double.parseDouble(new String(cArr, i10, i11 - i10));
        }
        return Double.doubleToRawLongBits(dTryDecFloatToDoubleTruncated);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharArray
    public long valueOfHexLiteral(char[] cArr, int i10, int i11, boolean z2, long j10, int i12, boolean z10, int i13) {
        double dTryHexFloatToDoubleTruncated = FastDoubleMath.tryHexFloatToDoubleTruncated(z2, j10, i12, z10, i13);
        if (Double.isNaN(dTryHexFloatToDoubleTruncated)) {
            dTryHexFloatToDoubleTruncated = Double.parseDouble(new String(cArr, i10, i11 - i10));
        }
        return Double.doubleToRawLongBits(dTryHexFloatToDoubleTruncated);
    }
}
