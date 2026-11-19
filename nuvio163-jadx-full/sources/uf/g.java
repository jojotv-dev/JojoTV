package uf;

/* JADX INFO: loaded from: classes4.dex */
public final class g implements a0.e {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final w.j f59780b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ int f59781c;

    public g(a0.e eVar, int i10) {
        this.f59781c = i10;
        this.f59780b = eVar.getScrollAnimationSpec();
    }

    @Override // a0.e
    public final float calculateScrollDistance(float f10, float f11, float f12) {
        float fAbs = Math.abs(f11);
        float f13 = this.f59781c;
        float f14 = f12 - f13;
        if (fAbs <= f12 && f14 < fAbs) {
            f13 = f12 - fAbs;
        }
        return f10 - f13;
    }

    @Override // a0.e
    public final w.j getScrollAnimationSpec() {
        return this.f59780b;
    }
}
