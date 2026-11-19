package dm;

/* JADX INFO: loaded from: classes5.dex */
public final class c extends bm.a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final byte[] f13406e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final byte[] f13407i;

    public c(a aVar, byte[] bArr) {
        super(false, (Object) aVar);
        if (bArr.length != aVar.f13401b) {
            throw new IllegalArgumentException("'encoding' has invalid length");
        }
        byte[] bArrH = rn.d.h(bArr, 0, 32);
        this.f13406e = bArrH;
        this.f13407i = rn.d.h(bArr, bArrH.length, bArr.length);
    }
}
