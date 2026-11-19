package ol;

import rn.d;

/* JADX INFO: loaded from: classes5.dex */
public final class c extends bm.a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final byte[] f43897e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final byte[] f43898i;

    public c(a aVar, byte[] bArr) {
        super(false, (Object) aVar);
        this.f43897e = d.h(bArr, 0, 32);
        this.f43898i = d.h(bArr, 32, bArr.length);
    }

    public c(a aVar, byte[] bArr, byte[] bArr2) {
        super(false, (Object) aVar);
        this.f43897e = d.b(bArr);
        this.f43898i = d.b(bArr2);
    }
}
