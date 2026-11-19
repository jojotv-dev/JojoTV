package sl;

import rn.d;

/* JADX INFO: loaded from: classes5.dex */
public final class c extends bm.a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final byte[] f56887e;

    public c(a aVar, byte[] bArr) {
        super(false, (Object) aVar);
        if (bArr.length != aVar.f56884e) {
            throw new IllegalArgumentException("'p' has invalid length");
        }
        this.f56887e = d.b(bArr);
    }
}
