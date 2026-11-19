package androidx.media3.exoplayer;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class a1 implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f2923b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ boolean f2924c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ boolean f2925e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ Object f2926i;

    public /* synthetic */ a1(Object obj, boolean z2, boolean z10, int i10) {
        this.f2923b = i10;
        this.f2926i = obj;
        this.f2924c = z2;
        this.f2925e = z10;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f2923b) {
            case 0:
                ((WakeLockManager) this.f2926i).lambda$setEnabled$0(this.f2924c, this.f2925e);
                break;
            default:
                ((WifiLockManager) this.f2926i).lambda$setEnabled$0(this.f2924c, this.f2925e);
                break;
        }
    }
}
