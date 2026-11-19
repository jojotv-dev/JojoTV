package qh;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/* JADX INFO: loaded from: classes5.dex */
public abstract class w {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final boolean f50788a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final int f50789b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final AtomicReference f50790c = new AtomicReference();

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final ConcurrentHashMap f50791d = new ConcurrentHashMap();

    static {
        boolean zEquals;
        int i10;
        try {
            String property = System.getProperty("rx2.purge-enabled");
            zEquals = property == null ? true : "true".equals(property);
        } catch (Throwable unused) {
        }
        f50788a = zEquals;
        if (zEquals) {
            try {
                String property2 = System.getProperty("rx2.purge-period-seconds");
                i10 = property2 == null ? 1 : Integer.parseInt(property2);
            } catch (Throwable unused2) {
            }
        }
        f50789b = i10;
        if (!f50788a) {
            return;
        }
        while (true) {
            AtomicReference atomicReference = f50790c;
            ScheduledExecutorService scheduledExecutorService = (ScheduledExecutorService) atomicReference.get();
            if (scheduledExecutorService != null) {
                return;
            }
            ScheduledExecutorService scheduledExecutorServiceNewScheduledThreadPool = Executors.newScheduledThreadPool(1, new s("RxSchedulerPurge"));
            do {
                if (atomicReference.compareAndSet(scheduledExecutorService, scheduledExecutorServiceNewScheduledThreadPool)) {
                    ao.e eVar = new ao.e(3);
                    long j10 = f50789b;
                    scheduledExecutorServiceNewScheduledThreadPool.scheduleAtFixedRate(eVar, j10, j10, TimeUnit.SECONDS);
                    return;
                }
            } while (atomicReference.get() == scheduledExecutorService);
            scheduledExecutorServiceNewScheduledThreadPool.shutdownNow();
        }
    }
}
