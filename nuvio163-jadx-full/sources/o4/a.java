package o4;

/* JADX INFO: loaded from: classes3.dex */
public final class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public int f43455a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f43456b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public float f43457c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public float f43458d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public long f43459e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public long f43460f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public long f43461g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public float f43462h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public int f43463i;

    public final float a(long j10) {
        long j11 = this.f43459e;
        if (j10 < j11) {
            return 0.0f;
        }
        long j12 = this.f43461g;
        if (j12 < 0 || j10 < j12) {
            return d.b((j10 - j11) / this.f43455a, 0.0f, 1.0f) * 0.5f;
        }
        float f10 = this.f43462h;
        return (d.b((j10 - j12) / this.f43463i, 0.0f, 1.0f) * f10) + (1.0f - f10);
    }
}
