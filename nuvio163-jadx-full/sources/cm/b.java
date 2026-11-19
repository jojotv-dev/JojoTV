package cm;

import rn.d;

/* JADX INFO: loaded from: classes5.dex */
public final class b extends bm.a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final byte[] f7393e;

    public b(a aVar, byte[] bArr) {
        super(true, (Object) aVar);
        if (bArr.length != aVar.f7392b) {
            throw new IllegalArgumentException("'sk' has invalid length");
        }
        this.f7393e = d.b(bArr);
    }
}
