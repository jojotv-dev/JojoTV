package kf;

/* JADX INFO: loaded from: classes4.dex */
public final class ed implements a0.e {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final w.j f28855b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ boolean f28856c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final /* synthetic */ int f28857d;

    public ed(a0.e eVar, boolean z2, int i10) {
        this.f28856c = z2;
        this.f28857d = i10;
        this.f28855b = eVar.getScrollAnimationSpec();
    }

    @Override // a0.e
    public final float calculateScrollDistance(float f10, float f11, float f12) {
        float fAbs = Math.abs(f11);
        boolean z2 = this.f28856c;
        int i10 = this.f28857d;
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
        if ((fAbs <= f12) && f15 < fAbs) {
            f14 = f12 - fAbs;
        }
        return f10 - f14;
    }

    @Override // a0.e
    public final w.j getScrollAnimationSpec() {
        return this.f28855b;
    }
}
