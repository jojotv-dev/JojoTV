package nh;

import java.util.concurrent.atomic.AtomicReference;

/* JADX INFO: loaded from: classes5.dex */
public final class d extends AtomicReference implements gh.b, Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final eh.a f42731b;

    public d(eh.a aVar) {
        this.f42731b = aVar;
    }

    @Override // gh.b
    public final boolean a() {
        return jh.a.c((gh.b) get());
    }

    @Override // gh.b
    public final void dispose() {
        jh.a.b(this);
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.f42731b.onComplete();
    }
}
