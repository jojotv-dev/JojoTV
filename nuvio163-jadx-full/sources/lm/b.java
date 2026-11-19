package lm;

import rn.d;

/* JADX INFO: loaded from: classes5.dex */
public final class b extends bm.a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final byte[] f34158e;

    public b(a aVar, byte[] bArr) {
        int iA;
        super(true, (Object) aVar);
        if (bArr == null) {
            throw new NullPointerException("encoded cannot be null");
        }
        int length = bArr.length;
        int i10 = aVar.f34157e;
        int i11 = aVar.f34155c;
        int i12 = aVar.f34156d;
        if (i10 == 2) {
            iA = 32;
        } else {
            iA = (aVar.a(i11) * i12 * i11) + ((((i12 + 1) * i12) / 2) * aVar.a(i11)) + (aVar.a(i12) * i11) + 32;
        }
        if (length != iA) {
            throw new IllegalArgumentException("secret key encoding wrong length for ".concat(aVar.f34153a));
        }
        this.f34158e = d.b(bArr);
        d.h(bArr, 0, 32);
    }
}
