package s3;

/* JADX INFO: loaded from: classes.dex */
public final class r {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f54036a;

    public final boolean equals(Object obj) {
        if (obj instanceof r) {
            return this.f54036a == ((r) obj).f54036a;
        }
        return false;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f54036a);
    }

    public final String toString() {
        int i10 = this.f54036a;
        if (i10 == 1) {
            return "Linearity.Linear";
        }
        if (i10 == 2) {
            return "Linearity.FontHinting";
        }
        return i10 == 3 ? "Linearity.None" : "Invalid";
    }
}
