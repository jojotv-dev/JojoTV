package qh;

import java.util.concurrent.atomic.AtomicBoolean;

/* JADX INFO: loaded from: classes5.dex */
public final class h extends AtomicBoolean implements Runnable, gh.b {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Runnable f50743b;

    public h(Runnable runnable) {
        this.f50743b = runnable;
    }

    @Override // gh.b
    public final boolean a() {
        return get();
    }

    @Override // gh.b
    public final void dispose() {
        lazySet(true);
    }

    @Override // java.lang.Runnable
    public final void run() {
        if (get()) {
            return;
        }
        try {
            this.f50743b.run();
        } finally {
            lazySet(true);
        }
    }
}
