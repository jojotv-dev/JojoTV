package qh;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/* JADX INFO: loaded from: classes5.dex */
public class q extends eh.j {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final ScheduledExecutorService f50778b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public volatile boolean f50779c;

    public q(ThreadFactory threadFactory) {
        boolean z2 = w.f50788a;
        ScheduledExecutorService scheduledExecutorServiceNewScheduledThreadPool = Executors.newScheduledThreadPool(1, threadFactory);
        if (w.f50788a && (scheduledExecutorServiceNewScheduledThreadPool instanceof ScheduledThreadPoolExecutor)) {
            w.f50791d.put((ScheduledThreadPoolExecutor) scheduledExecutorServiceNewScheduledThreadPool, scheduledExecutorServiceNewScheduledThreadPool);
        }
        this.f50778b = scheduledExecutorServiceNewScheduledThreadPool;
    }

    @Override // gh.b
    public final boolean a() {
        return this.f50779c;
    }

    @Override // eh.j
    public final gh.b c(Runnable runnable) {
        return d(runnable, 0L, null);
    }

    @Override // eh.j
    public final gh.b d(Runnable runnable, long j10, TimeUnit timeUnit) {
        return this.f50779c ? jh.b.f27500b : f(runnable, j10, timeUnit, null);
    }

    @Override // gh.b
    public final void dispose() {
        if (this.f50779c) {
            return;
        }
        this.f50779c = true;
        this.f50778b.shutdownNow();
    }

    public final v f(Runnable runnable, long j10, TimeUnit timeUnit, gh.a aVar) {
        v vVar = new v(runnable, aVar);
        if (aVar != null && !aVar.b(vVar)) {
            return vVar;
        }
        ScheduledExecutorService scheduledExecutorService = this.f50778b;
        try {
            vVar.b(j10 <= 0 ? scheduledExecutorService.submit((Callable) vVar) : scheduledExecutorService.schedule((Callable) vVar, j10, timeUnit));
            return vVar;
        } catch (RejectedExecutionException e10) {
            if (aVar != null) {
                aVar.d(vVar);
            }
            bo.a.H(e10);
            return vVar;
        }
    }
}
