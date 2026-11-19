package km;

import rn.d;

/* JADX INFO: loaded from: classes5.dex */
public final class c extends nk.b {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final byte[] f31678c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final a f31679e;

    public c(a aVar, byte[] bArr) {
        super(false);
        if (bArr.length != aVar.f31674b) {
            throw new IllegalArgumentException("'publicKey' has invalid length");
        }
        this.f31678c = d.b(bArr);
        this.f31679e = aVar;
    }
}
