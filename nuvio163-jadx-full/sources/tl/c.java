package tl;

import rn.d;

/* JADX INFO: loaded from: classes5.dex */
public final class c extends bm.a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final byte[] f57924e;

    public c(a aVar, byte[] bArr, int i10) {
        super(false, (Object) aVar);
        int i11 = 10 - aVar.f57922b;
        if (i10 != ((1 >> i11) * 842) + ((2 >> i11) * 574) + 450) {
            throw new IllegalArgumentException("'len' does not match public key size");
        }
        this.f57924e = d.h(bArr, 0, i10);
    }
}
