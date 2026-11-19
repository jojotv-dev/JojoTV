package qh;

import java.util.concurrent.atomic.AtomicReference;

/* JADX INFO: loaded from: classes5.dex */
public final class g extends AtomicReference implements Runnable, gh.b {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final gh.c f50741b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final gh.c f50742c;

    public g(Runnable runnable) {
        super(runnable);
        this.f50741b = new gh.c();
        this.f50742c = new gh.c();
    }

    @Override // gh.b
    public final boolean a() {
        return get() == null;
    }

    @Override // gh.b
    public final void dispose() {
        if (getAndSet(null) != null) {
            gh.c cVar = this.f50741b;
            cVar.getClass();
            jh.a.b(cVar);
            gh.c cVar2 = this.f50742c;
            cVar2.getClass();
            jh.a.b(cVar2);
        }
    }

    @Override // java.lang.Runnable
    public final void run() {
        gh.c cVar = this.f50742c;
        gh.c cVar2 = this.f50741b;
        jh.a aVar = jh.a.f27498b;
        Runnable runnable = (Runnable) get();
        if (runnable != null) {
            try {
                runnable.run();
            } finally {
                lazySet(null);
                cVar2.lazySet(aVar);
                cVar.lazySet(aVar);
            }
        }
    }
}
