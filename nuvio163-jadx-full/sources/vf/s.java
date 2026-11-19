package vf;

/* JADX INFO: loaded from: classes4.dex */
public final class s {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final float f60986a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final float f60987b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final float f60988c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final float f60989d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final float f60990e;

    public s(float f10, float f11, float f12, float f13, float f14) {
        this.f60986a = f10;
        this.f60987b = f11;
        this.f60988c = f12;
        this.f60989d = f13;
        this.f60990e = f14;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof s)) {
            return false;
        }
        s sVar = (s) obj;
        return t3.f.c(this.f60986a, sVar.f60986a) && t3.f.c(this.f60987b, sVar.f60987b) && t3.f.c(this.f60988c, sVar.f60988c) && t3.f.c(this.f60989d, sVar.f60989d) && t3.f.c(this.f60990e, sVar.f60990e) && Float.compare(0.42f, 0.42f) == 0 && Float.compare(0.62f, 0.62f) == 0 && Float.compare(0.72f, 0.72f) == 0;
    }

    public final int hashCode() {
        return Float.hashCode(0.72f) + o2.a.b(0.62f, o2.a.b(0.42f, o2.a.b(this.f60990e, o2.a.b(this.f60989d, o2.a.b(this.f60988c, o2.a.b(this.f60987b, Float.hashCode(this.f60986a) * 31, 31), 31), 31), 31), 31), 31);
    }

    public final String toString() {
        String strD = t3.f.d(this.f60986a);
        String strD2 = t3.f.d(this.f60987b);
        String strD3 = t3.f.d(this.f60988c);
        String strD4 = t3.f.d(this.f60989d);
        String strD5 = t3.f.d(this.f60990e);
        StringBuilder sbT = a0.i.t("NuvioLayoutTokens(tvSafeHorizontal=", strD, ", tvSafeVertical=", strD2, ", compactSafeHorizontal=");
        bd.e0.x(sbT, strD3, ", compactSafeVertical=", strD4, ", sidebarContentOffset=");
        return o2.a.q(sbT, strD5, ", rowAnchor=0.42, detailsHeroWidthFraction=0.62, detailsHeroHeightFraction=0.72)");
    }
}
