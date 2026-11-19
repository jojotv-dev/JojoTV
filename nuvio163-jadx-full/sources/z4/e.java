package z4;

/* JADX INFO: loaded from: classes3.dex */
public final class e {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f65494a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f65495b;

    public e(long j10, long j11) {
        if (j11 == 0) {
            this.f65494a = 0L;
            this.f65495b = 1L;
        } else {
            this.f65494a = j10;
            this.f65495b = j11;
        }
    }

    public final double a() {
        return this.f65494a / this.f65495b;
    }

    public final String toString() {
        return this.f65494a + "/" + this.f65495b;
    }
}
