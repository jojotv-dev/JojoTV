package m3;

/* JADX INFO: loaded from: classes.dex */
public final class m {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f34801a;

    public static String a(int i10) {
        if (i10 == -1) {
            return "Unspecified";
        }
        if (i10 == 0) {
            return "None";
        }
        if (i10 == 1) {
            return "Characters";
        }
        if (i10 == 2) {
            return "Words";
        }
        return i10 == 3 ? "Sentences" : "Invalid";
    }

    public final boolean equals(Object obj) {
        if (obj instanceof m) {
            return this.f34801a == ((m) obj).f34801a;
        }
        return false;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f34801a);
    }

    public final String toString() {
        return a(this.f34801a);
    }
}
