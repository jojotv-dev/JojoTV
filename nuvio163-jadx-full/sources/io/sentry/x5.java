package io.sentry;

/* JADX INFO: loaded from: classes.dex */
public final class x5 extends r4 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f26351b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final long f26352c;

    public x5(long j10, long j11) {
        this.f26351b = j10;
        this.f26352c = j11;
    }

    @Override // io.sentry.r4, java.lang.Comparable
    /* JADX INFO: renamed from: a */
    public final int compareTo(r4 r4Var) {
        if (!(r4Var instanceof x5)) {
            return super.compareTo(r4Var);
        }
        x5 x5Var = (x5) r4Var;
        long j10 = x5Var.f26351b;
        long j11 = this.f26351b;
        return j11 == j10 ? Long.compare(this.f26352c, x5Var.f26352c) : Long.compare(j11, j10);
    }

    @Override // io.sentry.r4
    public final long b(r4 r4Var) {
        return r4Var instanceof x5 ? this.f26352c - ((x5) r4Var).f26352c : super.b(r4Var);
    }

    @Override // io.sentry.r4
    public final long c(r4 r4Var) {
        if (!(r4Var instanceof x5)) {
            return super.c(r4Var);
        }
        x5 x5Var = (x5) r4Var;
        long j10 = x5Var.f26352c;
        int iCompareTo = compareTo(r4Var);
        long j11 = this.f26352c;
        if (iCompareTo < 0) {
            return d() + (j10 - j11);
        }
        return x5Var.d() + (j11 - j10);
    }

    @Override // io.sentry.r4
    public final long d() {
        return this.f26351b * 1000000;
    }

    public x5() {
        this(System.currentTimeMillis(), System.nanoTime());
    }
}
