package w;

/* JADX INFO: loaded from: classes.dex */
public final class a0 implements y {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f61151a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final v f61152b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final long f61153c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final long f61154d;

    public a0(int i10, int i11, v vVar) {
        this.f61151a = i10;
        this.f61152b = vVar;
        this.f61153c = ((long) i10) * 1000000;
        this.f61154d = ((long) i11) * 1000000;
    }

    @Override // w.y
    public final float c(long j10, float f10, float f11, float f12) {
        long j11 = j10 - this.f61154d;
        if (j11 < 0) {
            j11 = 0;
        }
        long j12 = this.f61153c;
        if (j11 > j12) {
            j11 = j12;
        }
        float fC = this.f61152b.c(this.f61151a == 0 ? 1.0f : j11 / j12);
        return (f11 * fC) + ((1 - fC) * f10);
    }

    @Override // w.y
    public final float d(long j10, float f10, float f11, float f12) {
        long j11 = j10 - this.f61154d;
        if (j11 < 0) {
            j11 = 0;
        }
        long j12 = this.f61153c;
        long j13 = j11 > j12 ? j12 : j11;
        if (j13 == 0) {
            return f12;
        }
        return (c(j13, f10, f11, f12) - c(j13 - 1000000, f10, f11, f12)) * 1000.0f;
    }

    @Override // w.y
    public final long e(float f10, float f11, float f12) {
        return this.f61154d + this.f61153c;
    }
}
