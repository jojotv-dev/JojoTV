package wl;

import rn.d;

/* JADX INFO: loaded from: classes5.dex */
public final class b extends bm.a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final byte[] f62645e;

    public b(a aVar, byte[] bArr) {
        super(true, (Object) aVar);
        if (bArr.length != aVar.f62643b) {
            throw new IllegalArgumentException("'seed_sk' has invalid length");
        }
        this.f62645e = d.b(bArr);
    }
}
