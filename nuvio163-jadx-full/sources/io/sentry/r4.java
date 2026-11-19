package io.sentry;

/* JADX INFO: loaded from: classes.dex */
public abstract class r4 implements Comparable {
    @Override // java.lang.Comparable
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public int compareTo(r4 r4Var) {
        return Long.compare(d(), r4Var.d());
    }

    public long b(r4 r4Var) {
        return d() - r4Var.d();
    }

    public long c(r4 r4Var) {
        return (r4Var == null || compareTo(r4Var) >= 0) ? d() : r4Var.d();
    }

    public abstract long d();
}
