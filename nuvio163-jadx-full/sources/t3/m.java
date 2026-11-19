package t3;

/* JADX INFO: loaded from: classes.dex */
public final class m {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f57170a;

    public static final boolean a(long j10, long j11) {
        return j10 == j11;
    }

    public static String b(long j10) {
        return ((int) (j10 >> 32)) + " x " + ((int) (j10 & 4294967295L));
    }

    public final boolean equals(Object obj) {
        if (obj instanceof m) {
            return this.f57170a == ((m) obj).f57170a;
        }
        return false;
    }

    public final int hashCode() {
        return Long.hashCode(this.f57170a);
    }

    public final String toString() {
        return b(this.f57170a);
    }
}
