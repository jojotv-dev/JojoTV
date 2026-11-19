package eh;

import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes5.dex */
public abstract class k {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final boolean f15680a = Boolean.getBoolean("rx2.scheduler.use-nanotime");

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final long f15681b = TimeUnit.MINUTES.toNanos(Long.getLong("rx2.scheduler.drift-tolerance", 15).longValue());

    public abstract j a();

    public gh.b b(Runnable runnable) {
        return c(runnable, 0L, TimeUnit.NANOSECONDS);
    }

    public gh.b c(Runnable runnable, long j10, TimeUnit timeUnit) {
        j jVarA = a();
        g gVar = new g(runnable, jVarA);
        jVarA.d(gVar, j10, timeUnit);
        return gVar;
    }

    public gh.b d(oh.h hVar, long j10, long j11, TimeUnit timeUnit) {
        j jVarA = a();
        h hVar2 = new h(hVar, jVarA, 0);
        gh.b bVarE = jVarA.e(hVar2, j10, j11, timeUnit);
        return bVarE == jh.b.f27500b ? bVarE : hVar2;
    }
}
