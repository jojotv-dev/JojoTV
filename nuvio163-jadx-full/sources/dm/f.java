package dm;

/* JADX INFO: loaded from: classes5.dex */
public final class f extends bm.a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final byte[] f13420e;

    public f(d dVar, byte[] bArr) {
        super(false, (Object) dVar);
        if (bArr.length != dVar.f13414b) {
            throw new IllegalArgumentException("'encH' has invalid length");
        }
        this.f13420e = rn.d.b(bArr);
    }
}
