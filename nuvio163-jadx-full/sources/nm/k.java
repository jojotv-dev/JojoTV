package nm;

/* JADX INFO: loaded from: classes5.dex */
public final class k extends n {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int f42975e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final int f42976f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public final int f42977g;

    public k(i iVar) {
        super(iVar);
        this.f42975e = iVar.f42969e;
        this.f42976f = iVar.f42970f;
        this.f42977g = iVar.f42971g;
    }

    @Override // nm.n
    public final byte[] a() {
        byte[] bArrA = super.a();
        rn.d.p(this.f42975e, 16, bArrA);
        rn.d.p(this.f42976f, 20, bArrA);
        rn.d.p(this.f42977g, 24, bArrA);
        return bArrA;
    }
}
