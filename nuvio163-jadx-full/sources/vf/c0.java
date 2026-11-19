package vf;

/* JADX INFO: loaded from: classes.dex */
public final class c0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final float f60790a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final float f60791b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final float f60792c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final float f60793d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final float f60794e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final float f60795f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public final float f60796g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public final float f60797h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final float f60798i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public final float f60799j;

    public c0(float f10, float f11, float f12, float f13, float f14, float f15, float f16, float f17, float f18, float f19) {
        this.f60790a = f10;
        this.f60791b = f11;
        this.f60792c = f12;
        this.f60793d = f13;
        this.f60794e = f14;
        this.f60795f = f15;
        this.f60796g = f16;
        this.f60797h = f17;
        this.f60798i = f18;
        this.f60799j = f19;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof c0)) {
            return false;
        }
        c0 c0Var = (c0) obj;
        return t3.f.c(this.f60790a, c0Var.f60790a) && t3.f.c(this.f60791b, c0Var.f60791b) && t3.f.c(this.f60792c, c0Var.f60792c) && t3.f.c(this.f60793d, c0Var.f60793d) && t3.f.c(this.f60794e, c0Var.f60794e) && t3.f.c(this.f60795f, c0Var.f60795f) && t3.f.c(this.f60796g, c0Var.f60796g) && t3.f.c(this.f60797h, c0Var.f60797h) && t3.f.c(this.f60798i, c0Var.f60798i) && t3.f.c(this.f60799j, c0Var.f60799j);
    }

    public final int hashCode() {
        return Float.hashCode(this.f60799j) + o2.a.b(this.f60798i, o2.a.b(this.f60797h, o2.a.b(this.f60796g, o2.a.b(this.f60795f, o2.a.b(this.f60794e, o2.a.b(this.f60793d, o2.a.b(this.f60792c, o2.a.b(this.f60791b, Float.hashCode(this.f60790a) * 31, 31), 31), 31), 31), 31), 31), 31), 31);
    }

    public final String toString() {
        String strD = t3.f.d(this.f60790a);
        String strD2 = t3.f.d(this.f60791b);
        String strD3 = t3.f.d(this.f60792c);
        String strD4 = t3.f.d(this.f60793d);
        String strD5 = t3.f.d(this.f60794e);
        String strD6 = t3.f.d(this.f60795f);
        String strD7 = t3.f.d(this.f60796g);
        String strD8 = t3.f.d(this.f60797h);
        String strD9 = t3.f.d(this.f60798i);
        String strD10 = t3.f.d(this.f60799j);
        StringBuilder sbT = a0.i.t("NuvioRadiusTokens(none=", strD, ", xxs=", strD2, ", xs=");
        bd.e0.x(sbT, strD3, ", sm=", strD4, ", md=");
        bd.e0.x(sbT, strD5, ", lg=", strD6, ", xl=");
        bd.e0.x(sbT, strD7, ", xxl=", strD8, ", panel=");
        return a0.i.q(sbT, strD9, ", full=", strD10, ")");
    }
}
