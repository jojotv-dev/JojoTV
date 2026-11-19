package w;

/* JADX INFO: loaded from: classes.dex */
public final class l extends p {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public float f61315a;

    public l(float f10) {
        this.f61315a = f10;
    }

    @Override // w.p
    public final float a(int i10) {
        if (i10 == 0) {
            return this.f61315a;
        }
        return 0.0f;
    }

    @Override // w.p
    public final int b() {
        return 1;
    }

    @Override // w.p
    public final p c() {
        return new l(0.0f);
    }

    @Override // w.p
    public final void d() {
        this.f61315a = 0.0f;
    }

    @Override // w.p
    public final void e(float f10, int i10) {
        if (i10 == 0) {
            this.f61315a = f10;
        }
    }

    public final boolean equals(Object obj) {
        return (obj instanceof l) && ((l) obj).f61315a == this.f61315a;
    }

    public final int hashCode() {
        return Float.hashCode(this.f61315a);
    }

    public final String toString() {
        return "AnimationVector1D: value = " + this.f61315a;
    }
}
