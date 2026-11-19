package qh;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/* JADX INFO: loaded from: classes5.dex */
public final class o extends eh.k {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final s f50768d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final s f50769e;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public static final n f50772h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final boolean f50773i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public static final l f50774j;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final AtomicReference f50775c;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final TimeUnit f50771g = TimeUnit.SECONDS;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static final long f50770f = Long.getLong("rx2.io-keep-alive-time", 60).longValue();

    static {
        n nVar = new n(new s("RxCachedThreadSchedulerShutdown"));
        f50772h = nVar;
        nVar.dispose();
        int iMax = Math.max(1, Math.min(10, Integer.getInteger("rx2.io-priority", 5).intValue()));
        s sVar = new s("RxCachedThreadScheduler", iMax, false);
        f50768d = sVar;
        f50769e = new s("RxCachedWorkerPoolEvictor", iMax, false);
        f50773i = Boolean.getBoolean("rx2.io-scheduled-release");
        l lVar = new l(0L, null, sVar);
        f50774j = lVar;
        lVar.f50759e.dispose();
        ScheduledFuture scheduledFuture = lVar.f50761m;
        if (scheduledFuture != null) {
            scheduledFuture.cancel(true);
        }
        ScheduledExecutorService scheduledExecutorService = lVar.f50760i;
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
        }
    }

    public o() {
        l lVar = f50774j;
        AtomicReference atomicReference = new AtomicReference(lVar);
        this.f50775c = atomicReference;
        l lVar2 = new l(f50770f, f50771g, f50768d);
        while (!atomicReference.compareAndSet(lVar, lVar2)) {
            if (atomicReference.get() != lVar) {
                lVar2.f50759e.dispose();
                ScheduledFuture scheduledFuture = lVar2.f50761m;
                if (scheduledFuture != null) {
                    scheduledFuture.cancel(true);
                }
                ScheduledExecutorService scheduledExecutorService = lVar2.f50760i;
                if (scheduledExecutorService != null) {
                    scheduledExecutorService.shutdownNow();
                    return;
                }
                return;
            }
        }
    }

    @Override // eh.k
    public final eh.j a() {
        return new m((l) this.f50775c.get());
    }
}
