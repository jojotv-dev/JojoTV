package com.fasterxml.jackson.core.io.doubleparser;

import java.nio.charset.StandardCharsets;

/* JADX INFO: loaded from: classes3.dex */
final class JavaFloatBitsFromByteArray extends AbstractJavaFloatingPointBitsFromByteArray {
    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromByteArray
    public long nan() {
        return Float.floatToRawIntBits(Float.NaN);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromByteArray
    public long negativeInfinity() {
        return Float.floatToRawIntBits(Float.NEGATIVE_INFINITY);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromByteArray
    public long positiveInfinity() {
        return Float.floatToRawIntBits(Float.POSITIVE_INFINITY);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromByteArray
    public long valueOfFloatLiteral(byte[] bArr, int i10, int i11, boolean z2, long j10, int i12, boolean z10, int i13) {
        float fTryDecFloatToFloatTruncated = FastFloatMath.tryDecFloatToFloatTruncated(z2, j10, i12, z10, i13);
        if (Float.isNaN(fTryDecFloatToFloatTruncated)) {
            fTryDecFloatToFloatTruncated = Float.parseFloat(new String(bArr, i10, i11 - i10, StandardCharsets.ISO_8859_1));
        }
        return Float.floatToRawIntBits(fTryDecFloatToFloatTruncated);
    }

    @Override // com.fasterxml.jackson.core.io.doubleparser.AbstractJavaFloatingPointBitsFromByteArray
    public long valueOfHexLiteral(byte[] bArr, int i10, int i11, boolean z2, long j10, int i12, boolean z10, int i13) {
        float fTryHexFloatToFloatTruncated = FastFloatMath.tryHexFloatToFloatTruncated(z2, j10, i12, z10, i13);
        if (Float.isNaN(fTryHexFloatToFloatTruncated)) {
            fTryHexFloatToFloatTruncated = Float.parseFloat(new String(bArr, i10, i11 - i10, StandardCharsets.ISO_8859_1));
        }
        return Float.floatToRawIntBits(fTryHexFloatToFloatTruncated);
    }
}
