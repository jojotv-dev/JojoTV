package i0;

/* JADX INFO: loaded from: classes.dex */
public final class i {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f23610a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f23611b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final t f23612c;

    public i(int i10, int i11, t tVar) {
        this.f23610a = i10;
        this.f23611b = i11;
        this.f23612c = tVar;
        if (i10 < 0) {
            d0.b.a("startIndex should be >= 0");
        }
        if (i11 > 0) {
            return;
        }
        d0.b.a("size should be > 0");
    }
}
