package t3;

/* JADX INFO: loaded from: classes.dex */
public final class d implements c {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final float f57152b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final float f57153c;

    public d(float f10, float f11) {
        this.f57152b = f10;
        this.f57153c = f11;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof d)) {
            return false;
        }
        d dVar = (d) obj;
        return Float.compare(this.f57152b, dVar.f57152b) == 0 && Float.compare(this.f57153c, dVar.f57153c) == 0;
    }

    @Override // t3.c
    public final float getDensity() {
        return this.f57152b;
    }

    public final int hashCode() {
        return Float.hashCode(this.f57153c) + (Float.hashCode(this.f57152b) * 31);
    }

    @Override // t3.c
    public final float l0() {
        return this.f57153c;
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder("DensityImpl(density=");
        sb2.append(this.f57152b);
        sb2.append(", fontScale=");
        return o2.a.o(sb2, this.f57153c, ')');
    }
}
