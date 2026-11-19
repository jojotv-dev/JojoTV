package im;

import rn.d;

/* JADX INFO: loaded from: classes5.dex */
public final class c extends nk.b {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final byte[] f24254c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final a f24255e;

    public c(a aVar, byte[] bArr) {
        super(false);
        int length = bArr.length;
        int i10 = aVar.f24250b;
        if (length != (((((i10 * i10) * i10) * aVar.f24251c) + 1) >>> 1) + 16) {
            throw new IllegalArgumentException("'publicKey' has invalid length");
        }
        this.f24254c = d.b(bArr);
        this.f24255e = aVar;
    }
}
