package e2;

/* JADX INFO: loaded from: classes.dex */
public abstract class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final long f13728a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final long f13729b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final long f13730c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final long f13731d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final /* synthetic */ int f13732e = 0;

    static {
        long j10 = 3;
        long j11 = j10 << 32;
        f13728a = (((long) 0) & 4294967295L) | j11;
        f13729b = (((long) 1) & 4294967295L) | j11;
        f13730c = j11 | (((long) 2) & 4294967295L);
        f13731d = (j10 & 4294967295L) | (((long) 4) << 32);
    }

    public static final boolean a(long j10, long j11) {
        return j10 == j11;
    }

    public static String b(long j10) {
        if (a(j10, f13728a)) {
            return "Rgb";
        }
        if (a(j10, f13729b)) {
            return "Xyz";
        }
        if (a(j10, f13730c)) {
            return "Lab";
        }
        return a(j10, f13731d) ? "Cmyk" : "Unknown";
    }
}
