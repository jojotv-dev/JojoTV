package io.sentry.android.core.performance;

/* JADX INFO: loaded from: classes.dex */
public final class c implements Comparable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final h f24906b = new h();

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final h f24907c = new h();

    @Override // java.lang.Comparable
    public final int compareTo(Object obj) {
        c cVar = (c) obj;
        int iCompare = Long.compare(this.f24906b.f24931e, cVar.f24906b.f24931e);
        return iCompare == 0 ? Long.compare(this.f24907c.f24931e, cVar.f24907c.f24931e) : iCompare;
    }
}
