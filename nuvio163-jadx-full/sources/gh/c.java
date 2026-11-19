package gh;

import java.util.concurrent.atomic.AtomicReference;

/* JADX INFO: loaded from: classes5.dex */
public final class c extends AtomicReference implements b {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f20190b = 1;

    public /* synthetic */ c() {
    }

    @Override // gh.b
    public final boolean a() {
        switch (this.f20190b) {
            case 0:
                return get() == null;
            default:
                return jh.a.c((b) get());
        }
    }

    @Override // gh.b
    public final void dispose() {
        Object andSet;
        switch (this.f20190b) {
            case 0:
                if (get() != null && (andSet = getAndSet(null)) != null) {
                    ((Runnable) andSet).run();
                    break;
                }
                break;
            default:
                jh.a.b(this);
                break;
        }
    }

    @Override // java.util.concurrent.atomic.AtomicReference
    public String toString() {
        switch (this.f20190b) {
            case 0:
                return "RunnableDisposable(disposed=" + a() + ", " + get() + ")";
            default:
                return super.toString();
        }
    }

    public /* synthetic */ c(Object obj) {
        super(obj);
    }

    public c(c cVar) {
        lazySet(cVar);
    }
}
