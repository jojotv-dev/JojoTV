package n7;

/* JADX INFO: loaded from: classes.dex */
public final class p {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f39676a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f39677b;

    public p(long j10, int i10) {
        this.f39677b = j10;
        this.f39676a = i10;
    }

    public static p a(int i10, int i11, String str) {
        if (i10 >= i11) {
            return null;
        }
        long j10 = 0;
        int i12 = i10;
        while (i12 < i11) {
            char cCharAt = str.charAt(i12);
            if (cCharAt < '0' || cCharAt > '9') {
                break;
            }
            j10 = (j10 * 10) + ((long) (cCharAt - '0'));
            if (j10 > 2147483647L) {
                return null;
            }
            i12++;
        }
        if (i12 == i10) {
            return null;
        }
        return new p(j10, i12);
    }
}
