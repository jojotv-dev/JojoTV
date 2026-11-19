package qh;

/* JADX INFO: loaded from: classes5.dex */
public final class a0 implements Comparable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Runnable f50718b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final long f50719c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int f50720e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public volatile boolean f50721i;

    public a0(Runnable runnable, Long l7, int i10) {
        this.f50718b = runnable;
        this.f50719c = l7.longValue();
        this.f50720e = i10;
    }

    @Override // java.lang.Comparable
    public final int compareTo(Object obj) {
        int i10;
        a0 a0Var = (a0) obj;
        long j10 = this.f50719c;
        long j11 = a0Var.f50719c;
        if (j10 < j11) {
            i10 = -1;
        } else {
            i10 = j10 > j11 ? 1 : 0;
        }
        if (i10 != 0) {
            return i10;
        }
        int i11 = a0Var.f50720e;
        int i12 = this.f50720e;
        if (i12 < i11) {
            return -1;
        }
        return i12 > i11 ? 1 : 0;
    }
}
