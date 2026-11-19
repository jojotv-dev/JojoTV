package tl;

import rn.d;

/* JADX INFO: loaded from: classes5.dex */
public final class b extends bm.a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final byte[] f57923e;

    public b(a aVar, byte[] bArr, int i10) {
        super(true, (Object) aVar);
        if (i10 != (11 << (aVar.f57922b - 5)) + 8) {
            throw new IllegalArgumentException("'len' does not match private key size");
        }
        this.f57923e = d.h(bArr, 0, i10);
    }
}
