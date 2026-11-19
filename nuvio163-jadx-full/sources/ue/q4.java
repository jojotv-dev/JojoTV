package ue;

/* JADX INFO: loaded from: classes.dex */
public final class q4 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final float f59183a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final float f59184b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final float f59185c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final float f59186d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final float f59187e;

    public q4(float f10, float f11, float f12, float f13, float f14) {
        this.f59183a = f10;
        this.f59184b = f11;
        this.f59185c = f12;
        this.f59186d = f13;
        this.f59187e = f14;
    }

    public static q4 a(q4 q4Var, float f10, float f11, float f12, int i10) {
        if ((i10 & 1) != 0) {
            f10 = q4Var.f59183a;
        }
        float f13 = f10;
        if ((i10 & 2) != 0) {
            f11 = q4Var.f59184b;
        }
        float f14 = f11;
        if ((i10 & 4) != 0) {
            f12 = q4Var.f59185c;
        }
        float f15 = q4Var.f59186d;
        float f16 = q4Var.f59187e;
        q4Var.getClass();
        return new q4(f13, f14, f12, f15, f16);
    }

    public final float b() {
        return this.f59184b;
    }

    public final float c() {
        return this.f59183a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof q4)) {
            return false;
        }
        q4 q4Var = (q4) obj;
        return t3.f.c(this.f59183a, q4Var.f59183a) && t3.f.c(this.f59184b, q4Var.f59184b) && t3.f.c(this.f59185c, q4Var.f59185c) && t3.f.c(this.f59186d, q4Var.f59186d) && Float.compare(this.f59187e, q4Var.f59187e) == 0;
    }

    public final int hashCode() {
        return Float.hashCode(this.f59187e) + o2.a.b(this.f59186d, o2.a.b(this.f59185c, o2.a.b(this.f59184b, Float.hashCode(this.f59183a) * 31, 31), 31), 31);
    }

    public final String toString() {
        String strD = t3.f.d(this.f59183a);
        String strD2 = t3.f.d(this.f59184b);
        String strD3 = t3.f.d(this.f59185c);
        String strD4 = t3.f.d(this.f59186d);
        StringBuilder sbT = a0.i.t("PosterCardStyle(width=", strD, ", height=", strD2, ", cornerRadius=");
        bd.e0.x(sbT, strD3, ", focusedBorderWidth=", strD4, ", focusedScale=");
        sbT.append(this.f59187e);
        sbT.append(")");
        return sbT.toString();
    }

    /* JADX WARN: Illegal instructions before constructor call */
    public q4(float f10, float f11, float f12, float f13, float f14, int i10) {
        float f15 = (i10 & 1) != 0 ? vf.j.f60895a.f60871a.f60810a : f10;
        float f16 = (i10 & 2) != 0 ? vf.j.f60895a.f60871a.f60811b : f11;
        float f17 = (i10 & 4) != 0 ? vf.j.f60895a.f60871a.f60812c : f12;
        float f18 = (i10 & 8) != 0 ? vf.j.f60895a.f60871a.f60814e : f13;
        if ((i10 & 16) != 0) {
            vf.j.f60895a.f60871a.getClass();
            f14 = 1.02f;
        }
        this(f15, f16, f17, f18, f14);
    }
}
