package pl;

import rn.d;

/* JADX INFO: loaded from: classes5.dex */
public final class c extends nk.b {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final a f45205c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final byte[] f45206e;

    public c(a aVar, byte[] bArr) {
        super(false);
        if (bArr.length == aVar.f45201b) {
            this.f45205c = aVar;
            this.f45206e = d.b(bArr);
        } else {
            throw new IllegalArgumentException("public key length must be " + aVar.f45201b + ", got " + bArr.length);
        }
    }
}
