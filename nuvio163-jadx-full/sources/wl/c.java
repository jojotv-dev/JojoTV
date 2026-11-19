package wl;

import rn.d;

/* JADX INFO: loaded from: classes5.dex */
public final class c extends bm.a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final byte[] f62646e;

    public c(a aVar, byte[] bArr) {
        super(false, (Object) aVar);
        if (bArr.length != aVar.f62644c) {
            throw new IllegalArgumentException("'p' has invalid length");
        }
        this.f62646e = d.b(bArr);
    }
}
