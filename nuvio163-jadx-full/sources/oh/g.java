package oh;

/* JADX INFO: loaded from: classes5.dex */
public final class g implements eh.f, gh.b {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final eh.a f43780b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public gh.b f43781c;

    public g(eh.a aVar) {
        this.f43780b = aVar;
    }

    @Override // gh.b
    public final boolean a() {
        return this.f43781c.a();
    }

    @Override // eh.f
    public final void d(gh.b bVar) {
        this.f43781c = bVar;
        this.f43780b.d(this);
    }

    @Override // gh.b
    public final void dispose() {
        this.f43781c.dispose();
    }

    @Override // eh.f
    public final void onComplete() {
        this.f43780b.onComplete();
    }

    @Override // eh.f
    public final void onError(Throwable th2) {
        this.f43780b.onError(th2);
    }

    @Override // eh.f
    public final void onNext(Object obj) {
    }
}
