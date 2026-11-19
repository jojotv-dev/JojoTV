package oh;

import java.util.concurrent.atomic.AtomicReference;

/* JADX INFO: loaded from: classes5.dex */
public final class h extends AtomicReference implements gh.b, Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final eh.f f43782b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public long f43783c;

    public h(eh.f fVar) {
        this.f43782b = fVar;
    }

    @Override // gh.b
    public final boolean a() {
        return get() == jh.a.f27498b;
    }

    @Override // gh.b
    public final void dispose() {
        jh.a.b(this);
    }

    @Override // java.lang.Runnable
    public final void run() {
        if (get() != jh.a.f27498b) {
            long j10 = this.f43783c;
            this.f43783c = 1 + j10;
            this.f43782b.onNext(Long.valueOf(j10));
        }
    }
}
