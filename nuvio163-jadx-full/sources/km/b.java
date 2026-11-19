package km;

import rn.d;

/* JADX INFO: loaded from: classes5.dex */
public final class b extends nk.b {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final byte[] f31676c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final a f31677e;

    public b(a aVar, byte[] bArr) {
        super(true);
        if (bArr.length != aVar.f31675c) {
            throw new IllegalArgumentException("'privateKey' has invalid length");
        }
        this.f31676c = d.b(bArr);
        this.f31677e = aVar;
    }
}
