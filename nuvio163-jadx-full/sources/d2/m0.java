package d2;

/* JADX INFO: loaded from: classes.dex */
public final class m0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f11748a;

    public final boolean equals(Object obj) {
        if (obj instanceof m0) {
            return this.f11748a == ((m0) obj).f11748a;
        }
        return false;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f11748a);
    }

    public final String toString() {
        int i10 = this.f11748a;
        if (i10 == 0) {
            return "Argb8888";
        }
        if (i10 == 1) {
            return "Alpha8";
        }
        if (i10 == 2) {
            return "Rgb565";
        }
        if (i10 == 3) {
            return "F16";
        }
        return i10 == 4 ? "Gpu" : "Unknown";
    }
}
