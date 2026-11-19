package com.fasterxml.jackson.core.io.doubleparser;

/* JADX INFO: loaded from: classes3.dex */
final class JavaFloatBitsFromCharSequence extends AbstractJavaFloatingPointBitsFromCharSequence {
    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharSequence
    public long nan() {
        return Float.floatToRawIntBits(Float.NaN);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharSequence
    public long negativeInfinity() {
        return Float.floatToRawIntBits(Float.NEGATIVE_INFINITY);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharSequence
    public long positiveInfinity() {
        return Float.floatToRawIntBits(Float.POSITIVE_INFINITY);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharSequence
    public long valueOfFloatLiteral(CharSequence charSequence, int i10, int i11, boolean z2, long j10, int i12, boolean z10, int i13) {
        float fTryDecFloatToFloatTruncated = FastFloatMath.tryDecFloatToFloatTruncated(z2, j10, i12, z10, i13);
        if (Float.isNaN(fTryDecFloatToFloatTruncated)) {
            fTryDecFloatToFloatTruncated = Float.parseFloat(charSequence.subSequence(i10, i11).toString());
        }
        return Float.floatToRawIntBits(fTryDecFloatToFloatTruncated);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromCharSequence
    public long valueOfHexLiteral(CharSequence charSequence, int i10, int i11, boolean z2, long j10, int i12, boolean z10, int i13) {
        float fTryHexFloatToFloatTruncated = FastFloatMath.tryHexFloatToFloatTruncated(z2, j10, i12, z10, i13);
        if (Float.isNaN(fTryHexFloatToFloatTruncated)) {
            fTryHexFloatToFloatTruncated = Float.parseFloat(charSequence.subSequence(i10, i11).toString());
        }
        return Float.floatToRawIntBits(fTryHexFloatToFloatTruncated);
    }
}
