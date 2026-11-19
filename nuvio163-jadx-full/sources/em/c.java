package em;

import rn.d;

/* JADX INFO: loaded from: classes5.dex */
public final class c extends bm.a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final byte[] f15802e;

    public c(a aVar, byte[] bArr) {
        super(false, (Object) aVar);
        if (bArr.length != aVar.f15799d) {
            throw new IllegalArgumentException("'pk' has invalid length");
        }
        this.f15802e = d.b(bArr);
    }
}
