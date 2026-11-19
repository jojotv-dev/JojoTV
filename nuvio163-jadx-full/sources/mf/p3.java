package mf;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class p3 implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f37622b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ q3 f37623c;

    public /* synthetic */ p3(q3 q3Var, int i10) {
        this.f37622b = i10;
        this.f37623c = q3Var;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f37622b) {
            case 0:
                q3 q3Var = this.f37623c;
                q3Var.c(q3Var.f37859w);
                break;
            default:
                q3 q3Var2 = this.f37623c;
                q3Var2.c(q3Var2.f37859w);
                break;
        }
    }
}
