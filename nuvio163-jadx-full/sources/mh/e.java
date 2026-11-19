package mh;

/* JADX INFO: loaded from: classes5.dex */
public final class e extends a {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final eh.f f39177b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public Object f39178c;

    public e(eh.f fVar) {
        this.f39177b = fVar;
    }

    @Override // gh.b
    public final boolean a() {
        return get() == 4;
    }

    @Override // lh.b
    public final void clear() {
        lazySet(32);
        this.f39178c = null;
    }

    @Override // gh.b
    public final void dispose() {
        set(4);
        this.f39178c = null;
    }

    @Override // lh.b
    public final boolean isEmpty() {
        return get() != 16;
    }

    @Override // lh.a
    public final int k() {
        lazySet(8);
        return 2;
    }

    @Override // lh.b
    public final Object poll() {
        if (get() != 16) {
            return null;
        }
        Object obj = this.f39178c;
        this.f39178c = null;
        lazySet(32);
        return obj;
    }
}
