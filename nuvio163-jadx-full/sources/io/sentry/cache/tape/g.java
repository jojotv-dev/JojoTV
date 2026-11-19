package io.sentry.cache.tape;

/* JADX INFO: loaded from: classes.dex */
public final class g {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final g f25426c = new g(0, 0);

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f25427a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f25428b;

    public g(long j10, int i10) {
        this.f25427a = j10;
        this.f25428b = i10;
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder();
        sb2.append(g.class.getSimpleName());
        sb2.append("[position=");
        sb2.append(this.f25427a);
        sb2.append(", length=");
        return h1.f.j(this.f25428b, "]", sb2);
    }
}
