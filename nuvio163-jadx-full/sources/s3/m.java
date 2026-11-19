package s3;

/* JADX INFO: loaded from: classes.dex */
public final class m {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f54028a;

    public static String a(int i10) {
        if (i10 == 1) {
            return "Ltr";
        }
        if (i10 == 2) {
            return "Rtl";
        }
        if (i10 == 3) {
            return "Content";
        }
        if (i10 == 4) {
            return "ContentOrLtr";
        }
        if (i10 == 5) {
            return "ContentOrRtl";
        }
        return i10 == 0 ? "Unspecified" : "Invalid";
    }

    public final boolean equals(Object obj) {
        if (obj instanceof m) {
            return this.f54028a == ((m) obj).f54028a;
        }
        return false;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f54028a);
    }

    public final String toString() {
        return a(this.f54028a);
    }
}
