package s3;

/* JADX INFO: loaded from: classes.dex */
public final class d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f54006a;

    public static String a(int i10) {
        if (i10 == 1) {
            return "Hyphens.None";
        }
        if (i10 == 2) {
            return "Hyphens.Auto";
        }
        return i10 == 0 ? "Hyphens.Unspecified" : "Invalid";
    }

    public final boolean equals(Object obj) {
        if (obj instanceof d) {
            return this.f54006a == ((d) obj).f54006a;
        }
        return false;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f54006a);
    }

    public final String toString() {
        return a(this.f54006a);
    }
}
