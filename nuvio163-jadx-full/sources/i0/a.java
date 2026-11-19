package i0;

/* JADX INFO: loaded from: classes.dex */
public final class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public boolean f23540a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public long f23541b;

    public final long a() {
        if (this.f23540a) {
            return Long.MAX_VALUE;
        }
        return Math.max(0L, this.f23541b - System.nanoTime());
    }
}
