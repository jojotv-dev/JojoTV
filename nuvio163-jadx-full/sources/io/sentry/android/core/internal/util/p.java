package io.sentry.android.core.internal.util;

/* JADX INFO: loaded from: classes5.dex */
public final class p implements Comparable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f24817b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final long f24818c;

    public p(long j10) {
        this(j10, j10);
    }

    @Override // java.lang.Comparable
    public final int compareTo(Object obj) {
        p pVar = (p) obj;
        int iCompare = Long.compare(this.f24818c, pVar.f24818c);
        return iCompare != 0 ? iCompare : Long.compare(this.f24817b, pVar.f24817b);
    }

    public p(long j10, long j11) {
        this.f24817b = j10;
        this.f24818c = j11;
    }
}
