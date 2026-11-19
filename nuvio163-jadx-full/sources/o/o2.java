package o;

/* JADX INFO: loaded from: classes.dex */
public final class o2 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public int f43268a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f43269b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f43270c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public int f43271d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f43272e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public int f43273f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public boolean f43274g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public boolean f43275h;

    public final void a(int i10, int i11) {
        this.f43270c = i10;
        this.f43271d = i11;
        this.f43275h = true;
        if (this.f43274g) {
            if (i11 != Integer.MIN_VALUE) {
                this.f43268a = i11;
            }
            if (i10 != Integer.MIN_VALUE) {
                this.f43269b = i10;
                return;
            }
            return;
        }
        if (i10 != Integer.MIN_VALUE) {
            this.f43268a = i10;
        }
        if (i11 != Integer.MIN_VALUE) {
            this.f43269b = i11;
        }
    }
}
