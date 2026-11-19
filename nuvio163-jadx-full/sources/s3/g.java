package s3;

/* JADX INFO: loaded from: classes.dex */
public final class g {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f54013a;

    public final boolean equals(Object obj) {
        if (obj instanceof g) {
            return this.f54013a == ((g) obj).f54013a;
        }
        return false;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f54013a);
    }

    public final String toString() {
        int i10 = this.f54013a;
        if (i10 == 0) {
            return "LineHeightStyle.Mode.Fixed";
        }
        if (i10 == 1) {
            return "LineHeightStyle.Mode.Minimum";
        }
        return i10 == 2 ? "LineHeightStyle.Mode.Tight" : "Invalid";
    }
}
