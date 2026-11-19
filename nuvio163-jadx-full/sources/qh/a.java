package qh;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;

/* JADX INFO: loaded from: classes5.dex */
public abstract class a extends AtomicReference implements gh.b {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final FutureTask f50714e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final FutureTask f50715i;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Runnable f50716b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public Thread f50717c;

    static {
        ao.e eVar = kh.a.f31455a;
        f50714e = new FutureTask(eVar, null);
        f50715i = new FutureTask(eVar, null);
    }

    public a(Runnable runnable) {
        this.f50716b = runnable;
    }

    @Override // gh.b
    public final boolean a() {
        Future future = (Future) get();
        return future == f50714e || future == f50715i;
    }

    public final void b(Future future) {
        Future future2;
        do {
            future2 = (Future) get();
            if (future2 == f50714e) {
                return;
            }
            if (future2 == f50715i) {
                future.cancel(this.f50717c != Thread.currentThread());
                return;
            }
        } while (!compareAndSet(future2, future));
    }

    @Override // gh.b
    public final void dispose() {
        FutureTask futureTask;
        Future future = (Future) get();
        if (future == f50714e || future == (futureTask = f50715i) || !compareAndSet(future, futureTask) || future == null) {
            return;
        }
        future.cancel(this.f50717c != Thread.currentThread());
    }
}
