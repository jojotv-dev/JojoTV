package sc;

import androidx.media3.common.ParserException;
import androidx.media3.extractor.ExtractorInput;

/* JADX INFO: loaded from: classes.dex */
public final class i {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final long[] f54260d = {128, 64, 32, 16, 8, 4, 2, 1};

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final byte[] f54261a = new byte[8];

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f54262b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f54263c;

    public static long a(byte[] bArr, int i10, boolean z2) {
        long j10 = ((long) bArr[0]) & 255;
        if (z2) {
            j10 &= ~f54260d[i10 - 1];
        }
        for (int i11 = 1; i11 < i10; i11++) {
            j10 = (j10 << 8) | (((long) bArr[i11]) & 255);
        }
        return j10;
    }

    public final long b(ExtractorInput extractorInput, boolean z2, boolean z10, int i10) throws ParserException {
        int i11;
        int i12 = this.f54262b;
        byte[] bArr = this.f54261a;
        if (i12 == 0) {
            if (!extractorInput.readFully(bArr, 0, 1, z2)) {
                return -1L;
            }
            int i13 = bArr[0] & 255;
            int i14 = 0;
            while (true) {
                if (i14 >= 8) {
                    i11 = -1;
                    break;
                }
                if ((f54260d[i14] & ((long) i13)) != 0) {
                    i11 = i14 + 1;
                    break;
                }
                i14++;
            }
            this.f54263c = i11;
            if (i11 == -1) {
                this.f54262b = 0;
                if (z2) {
                    return -2L;
                }
                throw ParserException.createForMalformedContainer("No valid varint length mask found", null);
            }
            this.f54262b = 1;
        }
        int i15 = this.f54263c;
        if (i15 > i10) {
            this.f54262b = 0;
            return -2L;
        }
        if (i15 != 1) {
            extractorInput.readFully(bArr, 1, i15 - 1);
        }
        this.f54262b = 0;
        return a(bArr, this.f54263c, z10);
    }
}
