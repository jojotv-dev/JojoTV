package w;

/* JADX INFO: loaded from: classes.dex */
public final class m extends p {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public float f61324a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public float f61325b;

    public m(float f10, float f11) {
        this.f61324a = f10;
        this.f61325b = f11;
    }

    @Override // w.p
    public final float a(int i10) {
        if (i10 == 0) {
            return this.f61324a;
        }
        if (i10 != 1) {
            return 0.0f;
        }
        return this.f61325b;
    }

    @Override // w.p
    public final int b() {
        return 2;
    }

    @Override // w.p
    public final p c() {
        return new m(0.0f, 0.0f);
    }

    @Override // w.p
    public final void d() {
        this.f61324a = 0.0f;
        this.f61325b = 0.0f;
    }

    @Override // w.p
    public final void e(float f10, int i10) {
        if (i10 == 0) {
            this.f61324a = f10;
        } else {
            if (i10 != 1) {
                return;
            }
            this.f61325b = f10;
        }
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof m)) {
            return false;
        }
        m mVar = (m) obj;
        return mVar.f61324a == this.f61324a && mVar.f61325b == this.f61325b;
    }

    public final int hashCode() {
        return Float.hashCode(this.f61325b) + (Float.hashCode(this.f61324a) * 31);
    }

    public final String toString() {
        return "AnimationVector2D: v1 = " + this.f61324a + ", v2 = " + this.f61325b;
    }
}
