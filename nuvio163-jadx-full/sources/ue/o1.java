package ue;

/* JADX INFO: loaded from: classes4.dex */
public final class o1 implements a0.e {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final w.j f59069b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ boolean f59070c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final /* synthetic */ int f59071d;

    public o1(a0.e eVar, boolean z2, int i10) {
        this.f59070c = z2;
        this.f59071d = i10;
        this.f59069b = eVar.getScrollAnimationSpec();
    }

    @Override // a0.e
    public final float calculateScrollDistance(float f10, float f11, float f12) {
        float fAbs = Math.abs(f11);
        boolean z2 = this.f59070c;
        int i10 = this.f59071d;
        if (z2) {
            boolean z10 = fAbs <= f12;
            float f13 = f12 - i10;
            if (!z10 || f13 >= fAbs) {
                fAbs = f13;
            }
            return (f10 + f11) - fAbs;
        }
        float f14 = i10;
        float f15 = f12 - f14;
        if (fAbs <= f12 && f15 < fAbs) {
            f14 = f12 - fAbs;
        }
        return f10 - f14;
    }

    @Override // a0.e
    public final w.j getScrollAnimationSpec() {
        return this.f59069b;
    }
}
