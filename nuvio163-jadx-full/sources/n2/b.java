package n2;

import q2.v;

/* JADX INFO: loaded from: classes3.dex */
public final class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f39454a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f39455b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final long f39456c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final boolean f39457d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final float f39458e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final long f39459f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public final long f39460g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public final boolean f39461h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public boolean f39462i;

    public b(long j10, long j11, long j12, boolean z2, float f10, long j13, long j14, boolean z10) {
        this.f39454a = j10;
        this.f39455b = j11;
        this.f39456c = j12;
        this.f39457d = z2;
        this.f39458e = f10;
        this.f39459f = j13;
        this.f39460g = j14;
        this.f39461h = z10;
    }

    public final void a() {
        this.f39462i = true;
    }

    public final long b() {
        return this.f39454a;
    }

    public final long c() {
        return this.f39456c;
    }

    public final boolean d() {
        return this.f39457d;
    }

    public final long e() {
        return this.f39460g;
    }

    public final boolean f() {
        return this.f39461h;
    }

    public final long g() {
        return this.f39455b;
    }

    public final boolean h() {
        return this.f39462i;
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder("IndirectPointerInputChange(id=");
        sb2.append((Object) v.h(this.f39454a));
        sb2.append(", uptimeMillis=");
        sb2.append(this.f39455b);
        sb2.append(", position=");
        sb2.append((Object) c2.b.g(this.f39456c));
        sb2.append(", pressed=");
        sb2.append(this.f39457d);
        sb2.append(", pressure=");
        sb2.append(this.f39458e);
        sb2.append(", previousUptimeMillis=");
        sb2.append(this.f39459f);
        sb2.append(", previousPosition=");
        sb2.append((Object) c2.b.g(this.f39460g));
        sb2.append(", previousPressed=");
        sb2.append(this.f39461h);
        sb2.append(", isConsumed=");
        return o2.a.s(sb2, this.f39462i, ')');
    }
}
