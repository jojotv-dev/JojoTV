package com.fasterxml.jackson.core.io.doubleparser;

/* JADX INFO: loaded from: classes3.dex */
final class JavaDoubleBitsFromCharSequence extends AbstractJavaFloatingPointBitsFromCharSequence {
    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharSequence
    public long nan() {
        return Double.doubleToRawLongBits(Double.NaN);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharSequence
    public long negativeInfinity() {
        return Double.doubleToRawLongBits(Double.NEGATIVE_INFINITY);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharSequence
    public long positiveInfinity() {
        return Double.doubleToRawLongBits(Double.POSITIVE_INFINITY);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharSequence
    public long valueOfFloatLiteral(CharSequence charSequence, int i10, int i11, boolean z2, long j10, int i12, boolean z10, int i13) {
        double dTryDecFloatToDoubleTruncated = FastDoubleMath.tryDecFloatToDoubleTruncated(z2, j10, i12, z10, i13);
        if (Double.isNaN(dTryDecFloatToDoubleTruncated)) {
            dTryDecFloatToDoubleTruncated = Double.parseDouble(charSequence.subSequence(i10, i11).toString());
        }
        return Double.doubleToRawLongBits(dTryDecFloatToDoubleTruncated);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharSequence
    public long valueOfHexLiteral(CharSequence charSequence, int i10, int i11, boolean z2, long j10, int i12, boolean z10, int i13) {
        double dTryHexFloatToDoubleTruncated = FastDoubleMath.tryHexFloatToDoubleTruncated(z2, j10, i12, z10, i13);
        if (Double.isNaN(dTryHexFloatToDoubleTruncated)) {
            dTryHexFloatToDoubleTruncated = Double.parseDouble(charSequence.subSequence(i10, i11).toString());
        }
        return Double.doubleToRawLongBits(dTryHexFloatToDoubleTruncated);
    }
}
