package yl;

import a3.h;
import androidx.media3.exoplayer.RendererCapabilities;
import dc.d1;

/* JADX INFO: loaded from: classes5.dex */
public final class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final h f65389a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f65390b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f65391c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final int f65392d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int f65393e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final int f65394f;

    public a(int i10) {
        this.f65390b = i10;
        if (i10 == 2) {
            this.f65392d = 3;
        } else {
            if (i10 != 3 && i10 != 4) {
                throw new IllegalArgumentException(d1.f("K: ", i10, " is not supported for ML-KEM"));
            }
            this.f65392d = 2;
        }
        int i11 = i10 * RendererCapabilities.DECODER_SUPPORT_MASK;
        this.f65391c = i11;
        this.f65393e = i11 + 32;
        this.f65394f = i11;
        this.f65389a = new h(this, 28);
    }

    public final boolean a(byte[] bArr) {
        int i10 = -1;
        for (int i11 = 0; i11 < this.f65390b; i11++) {
            int i12 = i11 * RendererCapabilities.DECODER_SUPPORT_MASK;
            int i13 = -1;
            for (int i14 = 0; i14 < 128; i14++) {
                int i15 = (i14 * 3) + i12;
                int i16 = bArr[i15] & 255;
                int i17 = bArr[i15 + 1] & 255;
                i13 = i13 & (((short) ((i16 | (i17 << 8)) & 4095)) - 3329) & (((short) ((((bArr[i15 + 2] & 255) << 4) | (i17 >> 4)) & 4095)) - 3329);
            }
            i10 &= i13;
        }
        return i10 < 0;
    }
}
