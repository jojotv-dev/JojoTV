package zl;

import rn.d;

/* JADX INFO: loaded from: classes5.dex */
public final class b extends bm.a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final byte[] f66843e;

    public b(a aVar, byte[] bArr) {
        super(true, (Object) aVar);
        if (bArr == null) {
            throw new NullPointerException("encoded cannot be null");
        }
        int length = bArr.length;
        int iA = aVar.a();
        int i10 = aVar.f66839c;
        int i11 = aVar.f66841e;
        if (length == ((i11 * i10) / 8) + iA) {
            this.f66843e = d.b(bArr);
            return;
        }
        throw new IllegalArgumentException("private key length wrong for " + aVar.f66837a + ": expected " + (((i11 * i10) / 8) + aVar.a()) + " bytes, got " + bArr.length);
    }
}
