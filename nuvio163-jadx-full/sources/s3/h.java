package s3;

/* JADX INFO: loaded from: classes.dex */
public final class h {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f54014a;

    public final boolean equals(Object obj) {
        if (obj instanceof h) {
            return this.f54014a == ((h) obj).f54014a;
        }
        return false;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f54014a);
    }

    public final String toString() {
        int i10 = this.f54014a;
        if (i10 == 1) {
            return "LineHeightStyle.Trim.FirstLineTop";
        }
        if (i10 == 16) {
            return "LineHeightStyle.Trim.LastLineBottom";
        }
        if (i10 == 17) {
            return "LineHeightStyle.Trim.Both";
        }
        return i10 == 0 ? "LineHeightStyle.Trim.None" : "Invalid";
    }
}
