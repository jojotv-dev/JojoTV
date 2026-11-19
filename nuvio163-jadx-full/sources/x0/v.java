package x0;

/* JADX INFO: loaded from: classes.dex */
public final class v {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f62939a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f62940b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f62941c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final int f62942d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int f62943e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final h3.p0 f62944f;

    public v(long j10, int i10, int i11, int i12, int i13, h3.p0 p0Var) {
        this.f62939a = j10;
        this.f62940b = i10;
        this.f62941c = i11;
        this.f62942d = i12;
        this.f62943e = i13;
        this.f62944f = p0Var;
    }

    public final x a(int i10) {
        return new x(uf.s.e(this.f62944f, i10), i10, this.f62939a);
    }

    public final f b() {
        int i10 = this.f62941c;
        int i11 = this.f62942d;
        if (i10 < i11) {
            return f.f62744c;
        }
        return i10 > i11 ? f.f62743b : f.f62745e;
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder("SelectionInfo(id=");
        sb2.append(this.f62939a);
        sb2.append(", range=(");
        int i10 = this.f62941c;
        sb2.append(i10);
        sb2.append('-');
        h3.p0 p0Var = this.f62944f;
        sb2.append(uf.s.e(p0Var, i10));
        sb2.append(',');
        int i11 = this.f62942d;
        sb2.append(i11);
        sb2.append('-');
        sb2.append(uf.s.e(p0Var, i11));
        sb2.append("), prevOffset=");
        return o2.a.p(sb2, this.f62943e, ')');
    }
}
