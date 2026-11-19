package nm;

/* JADX INFO: loaded from: classes5.dex */
public final class j extends n {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int f42972e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final int f42973f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public final int f42974g;

    public j(i iVar) {
        super(iVar);
        this.f42972e = iVar.f42969e;
        this.f42973f = iVar.f42970f;
        this.f42974g = iVar.f42971g;
    }

    @Override // nm.n
    public final byte[] a() {
        byte[] bArrA = super.a();
        rn.d.p(this.f42972e, 16, bArrA);
        rn.d.p(this.f42973f, 20, bArrA);
        rn.d.p(this.f42974g, 24, bArrA);
        return bArrA;
    }
}
