package ul;

import rn.d;

/* JADX INFO: loaded from: classes5.dex */
public final class c extends bm.a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final byte[] f59968e;

    public c(a aVar, byte[] bArr) {
        super(aVar);
        if (bArr.length != aVar.f59965c) {
            throw new IllegalArgumentException("'pk' has invalid length");
        }
        this.f59968e = d.b(bArr);
    }
}
