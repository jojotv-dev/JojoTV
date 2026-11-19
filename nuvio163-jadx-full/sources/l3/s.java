package l3;

/* JADX INFO: loaded from: classes.dex */
public final class s {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f31899a;

    public final boolean equals(Object obj) {
        if (obj instanceof s) {
            return this.f31899a == ((s) obj).f31899a;
        }
        return false;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f31899a);
    }

    public final String toString() {
        int i10 = this.f31899a;
        if (i10 == 0) {
            return "Normal";
        }
        return i10 == 1 ? "Italic" : "Invalid";
    }
}
