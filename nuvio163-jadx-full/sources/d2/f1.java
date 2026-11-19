package d2;

/* JADX INFO: loaded from: classes.dex */
public final class f1 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final long f11710b = j0.i(0.5f, 0.5f);

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final /* synthetic */ int f11711c = 0;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f11712a;

    public static final boolean a(long j10, long j11) {
        return j10 == j11;
    }

    public static final float b(long j10) {
        return Float.intBitsToFloat((int) (j10 >> 32));
    }

    public static final float c(long j10) {
        return Float.intBitsToFloat((int) (j10 & 4294967295L));
    }

    public static String d(long j10) {
        return "TransformOrigin(packedValue=" + j10 + ')';
    }

    public final boolean equals(Object obj) {
        if (obj instanceof f1) {
            return this.f11712a == ((f1) obj).f11712a;
        }
        return false;
    }

    public final int hashCode() {
        return Long.hashCode(this.f11712a);
    }

    public final String toString() {
        return d(this.f11712a);
    }
}
