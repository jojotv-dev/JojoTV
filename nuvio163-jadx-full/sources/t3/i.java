package t3;

/* JADX INFO: loaded from: classes.dex */
public final class i {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f57163a;

    public static final float a(long j10) {
        return Float.intBitsToFloat((int) (j10 & 4294967295L));
    }

    public static final float b(long j10) {
        return Float.intBitsToFloat((int) (j10 >> 32));
    }

    public final boolean equals(Object obj) {
        if (obj instanceof i) {
            return this.f57163a == ((i) obj).f57163a;
        }
        return false;
    }

    public final int hashCode() {
        return Long.hashCode(this.f57163a);
    }

    public final String toString() {
        long j10 = this.f57163a;
        if (j10 == 9205357640488583168L) {
            return "DpSize.Unspecified";
        }
        return ((Object) f.d(b(j10))) + " x " + ((Object) f.d(a(j10)));
    }
}
