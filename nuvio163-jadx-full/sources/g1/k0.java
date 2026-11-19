package g1;

/* JADX INFO: loaded from: classes.dex */
public final class k0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f18457a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ Object f18458b;

    public /* synthetic */ k0(Object obj, int i10) {
        this.f18457a = i10;
        this.f18458b = obj;
    }

    public final void a() {
        switch (this.f18457a) {
            case 0:
                ((l0) this.f18458b).A--;
                break;
            default:
                ((u1.w) this.f18458b).k--;
                break;
        }
    }

    public final void b() {
        switch (this.f18457a) {
            case 0:
                ((l0) this.f18458b).A++;
                break;
            default:
                ((u1.w) this.f18458b).k++;
                break;
        }
    }
}
