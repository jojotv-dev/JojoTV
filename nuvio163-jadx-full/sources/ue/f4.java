package ue;

/* JADX INFO: loaded from: classes.dex */
public final class f4 implements a0.e {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final w.f1 f58755b;

    public f4() {
        vf.u uVar = vf.t.f60996a.f61025a;
        this.f58755b = w.d.m(0.95f, 180, null, 4);
    }

    @Override // a0.e
    public final float calculateScrollDistance(float f10, float f11, float f12) {
        if (f12 <= 0.0f || f11 <= 0.0f) {
            return 0.0f;
        }
        float f13 = ((f11 / 2.0f) + f10) - (0.42f * f12);
        if (f10 < 0.0f || f10 + f11 > f12 || f13 <= 0.0f || f13 >= 12.0f) {
            return f13;
        }
        return 0.0f;
    }

    @Override // a0.e
    public final w.j getScrollAnimationSpec() {
        return this.f58755b;
    }
}
