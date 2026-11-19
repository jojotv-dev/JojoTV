package com.fasterxml.jackson.core.io.doubleparser;

import java.nio.charset.StandardCharsets;

/* JADX INFO: loaded from: classes3.dex */
final class JavaDoubleBitsFromByteArray extends AbstractJavaFloatingPointBitsFromByteArray {
    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromByteArray
    public long nan() {
        return Double.doubleToRawLongBits(Double.NaN);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromByteArray
    public long negativeInfinity() {
        return Double.doubleToRawLongBits(Double.NEGATIVE_INFINITY);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromByteArray
    public long positiveInfinity() {
        return Double.doubleToRawLongBits(Double.POSITIVE_INFINITY);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromByteArray
    public long valueOfFloatLiteral(byte[] bArr, int i10, int i11, boolean z2, long j10, int i12, boolean z10, int i13) {
        double dTryDecFloatToDoubleTruncated = FastDoubleMath.tryDecFloatToDoubleTruncated(z2, j10, i12, z10, i13);
        if (Double.isNaN(dTryDecFloatToDoubleTruncated)) {
            dTryDecFloatToDoubleTruncated = Double.parseDouble(new String(bArr, i10, i11 - i10, StandardCharsets.ISO_8859_1));
        }
        return Double.doubleToRawLongBits(dTryDecFloatToDoubleTruncated);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromByteArray
    public long valueOfHexLiteral(byte[] bArr, int i10, int i11, boolean z2, long j10, int i12, boolean z10, int i13) {
        double dTryHexFloatToDoubleTruncated = FastDoubleMath.tryHexFloatToDoubleTruncated(z2, j10, i12, z10, i13);
        if (Double.isNaN(dTryHexFloatToDoubleTruncated)) {
            dTryHexFloatToDoubleTruncated = Double.parseDouble(new String(bArr, i10, i11 - i10, StandardCharsets.ISO_8859_1));
        }
        return Double.doubleToRawLongBits(dTryHexFloatToDoubleTruncated);
    }
}
