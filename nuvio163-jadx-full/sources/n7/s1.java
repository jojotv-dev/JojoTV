package n7;

/* JADX INFO: loaded from: classes3.dex */
public final class s1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final float f39735a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final float f39736b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public float f39737c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public float f39738d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public boolean f39739e = false;

    public s1(float f10, float f11, float f12, float f13) {
        this.f39737c = 0.0f;
        this.f39738d = 0.0f;
        this.f39735a = f10;
        this.f39736b = f11;
        double dSqrt = Math.sqrt((f13 * f13) + (f12 * f12));
        if (dSqrt != 0.0d) {
            this.f39737c = (float) (((double) f12) / dSqrt);
            this.f39738d = (float) (((double) f13) / dSqrt);
        }
    }

    public final void a(float f10, float f11) {
        float f12 = f10 - this.f39735a;
        float f13 = f11 - this.f39736b;
        double dSqrt = Math.sqrt((f13 * f13) + (f12 * f12));
        if (dSqrt != 0.0d) {
            f12 = (float) (((double) f12) / dSqrt);
            f13 = (float) (((double) f13) / dSqrt);
        }
        float f14 = this.f39737c;
        if (f12 != (-f14) || f13 != (-this.f39738d)) {
            this.f39737c = f14 + f12;
            this.f39738d += f13;
        } else {
            this.f39739e = true;
            this.f39737c = -f13;
            this.f39738d = f12;
        }
    }

    public final void b(s1 s1Var) {
        float f10 = s1Var.f39737c;
        float f11 = this.f39737c;
        if (f10 == (-f11)) {
            float f12 = s1Var.f39738d;
            if (f12 == (-this.f39738d)) {
                this.f39739e = true;
                this.f39737c = -f12;
                this.f39738d = s1Var.f39737c;
                return;
            }
        }
        this.f39737c = f11 + f10;
        this.f39738d += s1Var.f39738d;
    }

    public final String toString() {
        return "(" + this.f39735a + "," + this.f39736b + " " + this.f39737c + "," + this.f39738d + ")";
    }
}
