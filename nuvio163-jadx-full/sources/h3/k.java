package h3;

/* JADX INFO: loaded from: classes.dex */
public final class k {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f20688a;

    public static String a(int i10) {
        if (i10 == 0) {
            return "EmojiSupportMatch.Default";
        }
        if (i10 == 1) {
            return "EmojiSupportMatch.None";
        }
        return i10 == 2 ? "EmojiSupportMatch.All" : h1.f.l("Invalid(value=", i10, ')');
    }

    public final boolean equals(Object obj) {
        if (obj instanceof k) {
            return this.f20688a == ((k) obj).f20688a;
        }
        return false;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f20688a);
    }

    public final String toString() {
        return a(this.f20688a);
    }
}
