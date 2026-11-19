package qh;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

/* JADX INFO: loaded from: classes5.dex */
public final class u extends a implements Callable {
    @Override // java.util.concurrent.Callable
    public final Object call() {
        FutureTask futureTask = a.f50714e;
        this.f50717c = Thread.currentThread();
        try {
            this.f50716b.run();
            return null;
        } finally {
            lazySet(futureTask);
            this.f50717c = null;
        }
    }
}
