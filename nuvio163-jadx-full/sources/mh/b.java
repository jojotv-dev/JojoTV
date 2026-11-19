package mh;

import java.util.concurrent.CountDownLatch;

/* JADX INFO: loaded from: classes5.dex */
public final class b extends CountDownLatch implements eh.f, gh.b {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public Object f39167b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public Throwable f39168c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public gh.b f39169e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public volatile boolean f39170i;

    @Override // gh.b
    public final boolean a() {
        return this.f39170i;
    }

    @Override // eh.f
    public final void d(gh.b bVar) {
        this.f39169e = bVar;
        if (this.f39170i) {
            bVar.dispose();
        }
    }

    @Override // gh.b
    public final void dispose() {
        this.f39170i = true;
        gh.b bVar = this.f39169e;
        if (bVar != null) {
            bVar.dispose();
        }
    }

    @Override // eh.f
    public final void onComplete() {
        countDown();
    }

    @Override // eh.f
    public final void onError(Throwable th2) {
        if (this.f39167b == null) {
            this.f39168c = th2;
        }
        countDown();
    }

    @Override // eh.f
    public final void onNext(Object obj) {
        if (this.f39167b == null) {
            this.f39167b = obj;
            this.f39169e.dispose();
            countDown();
        }
    }
}
