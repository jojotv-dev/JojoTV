package o7;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class d implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f43555b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ n1 f43556c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ int f43557e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ int f43558i;

    public /* synthetic */ d(n1 n1Var, int i10, int i11, int i12) {
        this.f43555b = i12;
        this.f43556c = n1Var;
        this.f43557e = i10;
        this.f43558i = i11;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f43555b) {
            case 0:
                n1 n1Var = this.f43556c;
                int i10 = this.f43557e;
                if (n1Var.x(i10)) {
                    return;
                }
                throw n1Var.m(this.f43558i, "capturing group < " + i10 + " > does not exist");
            default:
                n1 n1Var2 = this.f43556c;
                int i11 = this.f43557e;
                if (n1Var2.x(i11)) {
                    return;
                }
                throw n1Var2.m(this.f43558i, "Backreference to non-existent capturing group " + i11);
        }
    }
}
