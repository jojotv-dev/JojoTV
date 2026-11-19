package t3;

/* JADX INFO: loaded from: classes.dex */
public final class q {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f57179a;

    public static final boolean a(long j10, long j11) {
        return j10 == j11;
    }

    public static String b(long j10) {
        if (a(j10, 0L)) {
            return "Unspecified";
        }
        if (a(j10, 4294967296L)) {
            return "Sp";
        }
        return a(j10, 8589934592L) ? "Em" : "Invalid";
    }

    public final boolean equals(Object obj) {
        if (obj instanceof q) {
            return this.f57179a == ((q) obj).f57179a;
        }
        return false;
    }

    public final int hashCode() {
        return Long.hashCode(this.f57179a);
    }

    public final String toString() {
        return b(this.f57179a);
    }
}
