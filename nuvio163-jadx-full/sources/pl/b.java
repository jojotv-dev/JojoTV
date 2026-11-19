package pl;

import rn.d;

/* JADX INFO: loaded from: classes5.dex */
public final class b extends nk.b {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final a f45203c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final byte[] f45204e;

    public b(a aVar, byte[] bArr) {
        super(true);
        if (bArr.length == aVar.f45202c) {
            this.f45203c = aVar;
            this.f45204e = d.b(bArr);
        } else {
            throw new IllegalArgumentException("private key length must be " + aVar.f45202c + ", got " + bArr.length);
        }
    }
}
