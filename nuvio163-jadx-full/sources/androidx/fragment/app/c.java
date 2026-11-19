package androidx.fragment.app;

import c.j;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class c implements f.b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f2599a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ j f2600b;

    public /* synthetic */ c(j jVar, int i10) {
        this.f2599a = i10;
        this.f2600b = jVar;
    }

    @Override // f.b
    public final void a(j jVar) {
        switch (this.f2599a) {
            case 0:
                ((FragmentActivity) this.f2600b).lambda$init$3(jVar);
                break;
            default:
                j.g(this.f2600b, jVar);
                break;
        }
    }
}
