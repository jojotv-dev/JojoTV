package ul;

import rn.d;

/* JADX INFO: loaded from: classes5.dex */
public final class b extends bm.a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final byte[] f59967e;

    public b(a aVar, byte[] bArr) {
        super(aVar);
        if (bArr.length != aVar.f59966e) {
            throw new IllegalArgumentException("'sk' has invalid length");
        }
        this.f59967e = d.b(bArr);
    }
}
