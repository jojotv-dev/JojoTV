package mh;

import java.util.concurrent.CountDownLatch;

/* JADX INFO: loaded from: classes5.dex */
public final class c extends CountDownLatch implements eh.a {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public Throwable f39171b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public gh.b f39172c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public volatile boolean f39173e;

    @Override // eh.a
    public final void d(gh.b bVar) {
        this.f39172c = bVar;
        if (this.f39173e) {
            bVar.dispose();
        }
    }

    @Override // eh.a
    public final void onComplete() {
        countDown();
    }

    @Override // eh.a
    public final void onError(Throwable th2) {
        this.f39171b = th2;
        countDown();
    }
}
