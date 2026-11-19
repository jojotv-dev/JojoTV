package t3;

/* JADX INFO: loaded from: classes.dex */
public final class g {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f57158a;

    public static String a(long j10) {
        if (j10 == 9205357640488583168L) {
            return "DpOffset.Unspecified";
        }
        return "(" + ((Object) f.d(Float.intBitsToFloat((int) (j10 >> 32)))) + ", " + ((Object) f.d(Float.intBitsToFloat((int) (j10 & 4294967295L)))) + ')';
    }

    public final boolean equals(Object obj) {
        if (obj instanceof g) {
            return this.f57158a == ((g) obj).f57158a;
        }
        return false;
    }

    public final int hashCode() {
        return Long.hashCode(this.f57158a);
    }

    public final String toString() {
        return a(this.f57158a);
    }
}
