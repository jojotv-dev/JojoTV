package zl;

import rn.d;

/* JADX INFO: loaded from: classes5.dex */
public final class c extends bm.a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final byte[] f66844e;

    public c(a aVar, byte[] bArr) {
        super(false, (Object) aVar);
        if (bArr.length == aVar.a()) {
            this.f66844e = d.b(bArr);
            return;
        }
        throw new IllegalArgumentException("public key length wrong for " + aVar.f66837a + ": expected " + aVar.a() + " bytes, got " + bArr.length);
    }
}
