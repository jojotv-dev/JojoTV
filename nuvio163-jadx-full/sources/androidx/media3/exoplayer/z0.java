package androidx.media3.exoplayer;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class z0 implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3411b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ boolean f3412c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Object f3413e;

    public /* synthetic */ z0(Object obj, boolean z2, int i10) {
        this.f3411b = i10;
        this.f3413e = obj;
        this.f3412c = z2;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3411b) {
            case 0:
                ((WakeLockManager) this.f3413e).lambda$setStayAwake$1(this.f3412c);
                break;
            default:
                ((WifiLockManager) this.f3413e).lambda$setStayAwake$1(this.f3412c);
                break;
        }
    }
}
