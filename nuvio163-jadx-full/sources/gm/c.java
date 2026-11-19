package gm;

import rn.d;

/* JADX INFO: loaded from: classes5.dex */
public final class c extends nk.b {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final a f20417c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final byte[] f20418e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final byte[] f20419i;

    public c(a aVar, byte[] bArr) {
        super(false);
        int i10 = aVar.f20410g;
        if (bArr.length != (aVar.f20406c - aVar.f20407d) + i10) {
            throw new IllegalArgumentException("encoded length mismatch");
        }
        this.f20417c = aVar;
        this.f20418e = d.h(bArr, 0, i10);
        this.f20419i = d.h(bArr, i10, bArr.length);
    }
}
