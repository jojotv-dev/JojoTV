package lm;

import rn.d;

/* JADX INFO: loaded from: classes5.dex */
public final class c extends bm.a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final byte[] f34159e;

    public c(a aVar, byte[] bArr) {
        int iA;
        super(false, (Object) aVar);
        int length = bArr.length;
        int i10 = aVar.f34157e;
        int i11 = aVar.f34156d;
        int i12 = aVar.f34155c;
        if (i10 != 0) {
            iA = ((((i12 + 1) * i12) / 2) * aVar.a(i12)) + 16;
        } else {
            iA = ((((i12 + 1) * i12) / 2) * aVar.a(i12)) + (aVar.a(i12) * i11 * i12) + ((((i11 + 1) * i11) / 2) * aVar.a(i12));
        }
        if (length != iA) {
            throw new IllegalArgumentException("public key encoding wrong length for ".concat(aVar.f34153a));
        }
        this.f34159e = d.b(bArr);
    }
}
