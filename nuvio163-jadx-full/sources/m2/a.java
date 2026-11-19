package m2;

/* JADX INFO: loaded from: classes.dex */
public final class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f34725a;

    public final boolean equals(Object obj) {
        if (obj instanceof a) {
            return this.f34725a == ((a) obj).f34725a;
        }
        return false;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f34725a);
    }

    public final String toString() {
        int i10 = this.f34725a;
        if (i10 == 1) {
            return "Touch";
        }
        return i10 == 2 ? "Keyboard" : "Error";
    }
}
