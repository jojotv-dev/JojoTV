package nm;

/* JADX INFO: loaded from: classes5.dex */
public final class h extends n {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int f42967e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final int f42968f;

    public h(g gVar) {
        super(gVar);
        this.f42967e = gVar.f42965e;
        this.f42968f = gVar.f42966f;
    }

    @Override // nm.n
    public final byte[] a() {
        byte[] bArrA = super.a();
        rn.d.p(0, 16, bArrA);
        rn.d.p(this.f42967e, 20, bArrA);
        rn.d.p(this.f42968f, 24, bArrA);
        return bArrA;
    }
}
