package t3;

/* JADX INFO: loaded from: classes.dex */
public final class f implements Comparable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final float f57157b;

    public /* synthetic */ f(float f10) {
        this.f57157b = f10;
    }

    public static final /* synthetic */ f a(float f10) {
        return new f(f10);
    }

    public static int b(float f10, float f11) {
        if (Float.isNaN(f10) || Float.isNaN(f11)) {
            return 0;
        }
        return Float.compare(f10, f11);
    }

    public static final boolean c(float f10, float f11) {
        return Float.compare(f10, f11) == 0;
    }

    public static String d(float f10) {
        if (Float.isNaN(f10)) {
            return "Dp.Unspecified";
        }
        return f10 + ".dp";
    }

    @Override // java.lang.Comparable
    public final int compareTo(Object obj) {
        return b(this.f57157b, ((f) obj).f57157b);
    }

    public final /* synthetic */ float e() {
        return this.f57157b;
    }

    public final boolean equals(Object obj) {
        if (obj instanceof f) {
            return Float.compare(this.f57157b, ((f) obj).f57157b) == 0;
        }
        return false;
    }

    public final int hashCode() {
        return Float.hashCode(this.f57157b);
    }

    public final String toString() {
        return d(this.f57157b);
    }
}
