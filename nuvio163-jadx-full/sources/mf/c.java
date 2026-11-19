package mf;

/* JADX INFO: loaded from: classes.dex */
public final class c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final float f35852a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final float f35853b;

    public c(float f10, float f11) {
        this.f35852a = f10;
        this.f35853b = f11;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof c)) {
            return false;
        }
        c cVar = (c) obj;
        return Float.compare(this.f35852a, cVar.f35852a) == 0 && Float.compare(this.f35853b, cVar.f35853b) == 0;
    }

    public final int hashCode() {
        return Float.hashCode(this.f35853b) + (Float.hashCode(this.f35852a) * 31);
    }

    public final String toString() {
        return "AspectScale(scaleX=" + this.f35852a + ", scaleY=" + this.f35853b + ")";
    }
}
