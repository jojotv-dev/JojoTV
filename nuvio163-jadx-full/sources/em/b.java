package em;

import rn.d;

/* JADX INFO: loaded from: classes5.dex */
public final class b extends bm.a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final byte[] f15801e;

    public b(a aVar, byte[] bArr) {
        super(true, (Object) aVar);
        if (bArr.length != aVar.f15800e) {
            throw new IllegalArgumentException("'sk' has invalid length");
        }
        this.f15801e = d.b(bArr);
    }
}
