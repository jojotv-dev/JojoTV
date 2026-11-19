package m3;

/* JADX INFO: loaded from: classes.dex */
public final class k {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f34793a;

    public static String a(int i10) {
        if (i10 == -1) {
            return "Unspecified";
        }
        if (i10 == 0) {
            return "None";
        }
        if (i10 == 1) {
            return "Default";
        }
        if (i10 == 2) {
            return "Go";
        }
        if (i10 == 3) {
            return "Search";
        }
        if (i10 == 4) {
            return "Send";
        }
        if (i10 == 5) {
            return "Previous";
        }
        if (i10 == 6) {
            return "Next";
        }
        return i10 == 7 ? "Done" : "Invalid";
    }

    public final boolean equals(Object obj) {
        if (obj instanceof k) {
            return this.f34793a == ((k) obj).f34793a;
        }
        return false;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f34793a);
    }

    public final String toString() {
        return a(this.f34793a);
    }
}
