package androidx.media3.exoplayer.source.preload;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class n implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3319b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ PreCacheHelper.ReleasableExecutorSupplier f3320c;

    public /* synthetic */ n(PreCacheHelper.ReleasableExecutorSupplier releasableExecutorSupplier, int i10) {
        this.f3319b = i10;
        this.f3320c = releasableExecutorSupplier;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3319b) {
            case 0:
                this.f3320c.lambda$onExecutorReleased$0();
                break;
            default:
                this.f3320c.onExecutorReleased();
                break;
        }
    }
}
