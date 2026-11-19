package io.sentry.android.core;

/* JADX INFO: loaded from: classes5.dex */
public final class t1 implements Comparable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f24963b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final long f24964c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final long f24965e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final long f24966i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final boolean f24967m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public final boolean f24968n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public final long f24969o;

    public t1(long j10) {
        this(j10, j10, 0L, 0L, false, false, 0L);
    }

    @Override // java.lang.Comparable
    public final int compareTo(Object obj) {
        return Long.compare(this.f24964c, ((t1) obj).f24964c);
    }

    public t1(long j10, long j11, long j12, long j13, boolean z2, boolean z10, long j14) {
        this.f24963b = j10;
        this.f24964c = j11;
        this.f24965e = j12;
        this.f24966i = j13;
        this.f24967m = z2;
        this.f24968n = z10;
        this.f24969o = j14;
    }
}
