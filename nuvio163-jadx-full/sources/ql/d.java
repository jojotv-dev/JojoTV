package ql;

/* JADX INFO: loaded from: classes5.dex */
public final class d extends bm.a {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final byte[] f50940e;

    public d(b bVar, byte[] bArr) {
        super(false, (Object) bVar);
        if (bArr.length != ((1 << bVar.f50935c) * 14) / 8) {
            throw new IllegalArgumentException("'H' has invalid length");
        }
        this.f50940e = rn.d.b(bArr);
    }
}
