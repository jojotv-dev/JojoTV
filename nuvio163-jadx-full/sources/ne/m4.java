package ne;

/* JADX INFO: loaded from: classes4.dex */
public final class m4 implements a0.e {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final w.j f40983b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ float f40984c;

    public m4(a0.e eVar, float f10) {
        this.f40984c = f10;
        this.f40983b = eVar.getScrollAnimationSpec();
    }

    @Override // a0.e
    public final float calculateScrollDistance(float f10, float f11, float f12) {
        float fAbs = Math.abs(f11);
        float f13 = this.f40984c;
        if (fAbs <= f12 && f12 - f13 < fAbs) {
            f13 = f12 - fAbs;
        }
        return f10 - f13;
    }

    @Override // a0.e
    public final w.j getScrollAnimationSpec() {
        return this.f40983b;
    }
}
