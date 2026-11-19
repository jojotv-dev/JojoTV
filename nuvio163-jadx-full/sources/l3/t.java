package l3;

/* JADX INFO: loaded from: classes.dex */
public final class t {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f31900a;

    public final boolean equals(Object obj) {
        if (obj instanceof t) {
            return this.f31900a == ((t) obj).f31900a;
        }
        return false;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f31900a);
    }

    public final String toString() {
        int i10 = this.f31900a;
        if (i10 == 0) {
            return "None";
        }
        if (i10 == 1) {
            return "Weight";
        }
        if (i10 == 2) {
            return "Style";
        }
        return i10 == 65535 ? "All" : "Invalid";
    }
}
