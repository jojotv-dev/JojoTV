package vf;

/* JADX INFO: loaded from: classes4.dex */
public final class m {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final float f60920a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final float f60921b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final float f60922c;

    public m(float f10, float f11, float f12) {
        this.f60920a = f10;
        this.f60921b = f11;
        this.f60922c = f12;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof m)) {
            return false;
        }
        m mVar = (m) obj;
        return t3.f.c(this.f60920a, mVar.f60920a) && t3.f.c(this.f60921b, mVar.f60921b) && t3.f.c(this.f60922c, mVar.f60922c) && Float.compare(0.28f, 0.28f) == 0 && Float.compare(0.52f, 0.52f) == 0 && Float.compare(0.78f, 0.78f) == 0 && Float.compare(0.18f, 0.18f) == 0 && Float.compare(0.38f, 0.38f) == 0 && Float.compare(0.62f, 0.62f) == 0 && Float.compare(0.42f, 0.42f) == 0 && Float.compare(0.08f, 0.08f) == 0 && Float.compare(0.18f, 0.18f) == 0;
    }

    public final int hashCode() {
        return Float.hashCode(0.18f) + o2.a.b(0.08f, o2.a.b(0.42f, o2.a.b(0.62f, o2.a.b(0.38f, o2.a.b(0.18f, o2.a.b(0.78f, o2.a.b(0.52f, o2.a.b(0.28f, o2.a.b(this.f60922c, o2.a.b(this.f60921b, Float.hashCode(this.f60920a) * 31, 31), 31), 31), 31), 31), 31), 31), 31), 31), 31);
    }

    public final String toString() {
        String strD = t3.f.d(this.f60920a);
        String strD2 = t3.f.d(this.f60921b);
        return o2.a.q(a0.i.t("NuvioEffectTokens(blurSoft=", strD, ", blurPanel=", strD2, ", blurStrong="), t3.f.d(this.f60922c), ", scrimLightAlpha=0.28, scrimMediumAlpha=0.52, scrimStrongAlpha=0.78, glowSoftAlpha=0.18, glowStrongAlpha=0.38, imageOverlayAlpha=0.62, disabledAlpha=0.42, shimmerLowAlpha=0.08, shimmerHighAlpha=0.18)");
    }
}
