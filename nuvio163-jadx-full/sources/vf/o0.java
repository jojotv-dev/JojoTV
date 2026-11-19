package vf;

/* JADX INFO: loaded from: classes.dex */
public final class o0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final float f60949a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final float f60950b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final float f60951c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final float f60952d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final float f60953e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final float f60954f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public final float f60955g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public final float f60956h;

    public o0(float f10, float f11, float f12, float f13, float f14, float f15, float f16, float f17) {
        this.f60949a = f10;
        this.f60950b = f11;
        this.f60951c = f12;
        this.f60952d = f13;
        this.f60953e = f14;
        this.f60954f = f15;
        this.f60955g = f16;
        this.f60956h = f17;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof o0)) {
            return false;
        }
        o0 o0Var = (o0) obj;
        return t3.f.c(this.f60949a, o0Var.f60949a) && t3.f.c(this.f60950b, o0Var.f60950b) && t3.f.c(this.f60951c, o0Var.f60951c) && t3.f.c(this.f60952d, o0Var.f60952d) && t3.f.c(this.f60953e, o0Var.f60953e) && t3.f.c(this.f60954f, o0Var.f60954f) && t3.f.c(this.f60955g, o0Var.f60955g) && t3.f.c(this.f60956h, o0Var.f60956h);
    }

    public final int hashCode() {
        return Float.hashCode(this.f60956h) + o2.a.b(this.f60955g, o2.a.b(this.f60954f, o2.a.b(this.f60953e, o2.a.b(this.f60952d, o2.a.b(this.f60951c, o2.a.b(this.f60950b, Float.hashCode(this.f60949a) * 31, 31), 31), 31), 31), 31), 31);
    }

    public final String toString() {
        String strD = t3.f.d(this.f60949a);
        String strD2 = t3.f.d(this.f60950b);
        String strD3 = t3.f.d(this.f60951c);
        String strD4 = t3.f.d(this.f60952d);
        String strD5 = t3.f.d(this.f60953e);
        String strD6 = t3.f.d(this.f60954f);
        String strD7 = t3.f.d(this.f60955g);
        String strD8 = t3.f.d(this.f60956h);
        StringBuilder sbT = a0.i.t("NuvioStrokeTokens(none=", strD, ", hairline=", strD2, ", thin=");
        bd.e0.x(sbT, strD3, ", medium=", strD4, ", focus=");
        bd.e0.x(sbT, strD5, ", heavy=", strD6, ", progress=");
        return a0.i.q(sbT, strD7, ", divider=", strD8, ")");
    }
}
