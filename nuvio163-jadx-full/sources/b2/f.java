package b2;

/* JADX INFO: loaded from: classes.dex */
public final class f {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f4224a;

    public static String a(int i10) {
        if (i10 == 1) {
            return "Next";
        }
        if (i10 == 2) {
            return "Previous";
        }
        if (i10 == 3) {
            return "Left";
        }
        if (i10 == 4) {
            return "Right";
        }
        if (i10 == 5) {
            return "Up";
        }
        if (i10 == 6) {
            return "Down";
        }
        if (i10 == 7) {
            return "Enter";
        }
        return i10 == 8 ? "Exit" : "Invalid FocusDirection";
    }

    public final boolean equals(Object obj) {
        if (obj instanceof f) {
            return this.f4224a == ((f) obj).f4224a;
        }
        return false;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f4224a);
    }

    public final String toString() {
        return a(this.f4224a);
    }
}
