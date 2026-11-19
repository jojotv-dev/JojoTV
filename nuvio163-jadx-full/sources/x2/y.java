package x2;

/* JADX INFO: loaded from: classes.dex */
public final class y implements e3.d0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public boolean f63316b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ d2.c1 f63317c;

    public y(d2.c1 c1Var) {
        this.f63317c = c1Var;
    }

    @Override // e3.d0
    public final void a(e3.c0 c0Var, Object obj) {
        if (obj == this.f63317c) {
            this.f63316b = true;
        }
    }
}
