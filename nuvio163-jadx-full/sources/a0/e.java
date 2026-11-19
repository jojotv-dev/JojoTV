package a0;

/* JADX INFO: loaded from: classes.dex */
public interface e {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final d f95a = d.f81a;

    default float calculateScrollDistance(float f10, float f11, float f12) {
        f95a.getClass();
        float f13 = f11 + f10;
        if ((f10 >= 0.0f && f13 <= f12) || (f10 < 0.0f && f13 > f12)) {
            return 0.0f;
        }
        float f14 = f13 - f12;
        return Math.abs(f10) < Math.abs(f14) ? f10 : f14;
    }

    default w.j getScrollAnimationSpec() {
        f95a.getClass();
        return d.f82b;
    }
}
