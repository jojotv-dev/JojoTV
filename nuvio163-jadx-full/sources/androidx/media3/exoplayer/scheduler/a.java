package androidx.media3.exoplayer.scheduler;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class a implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3246b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ RequirementsWatcher.NetworkCallback f3247c;

    public /* synthetic */ a(RequirementsWatcher.NetworkCallback networkCallback, int i10) {
        this.f3246b = i10;
        this.f3247c = networkCallback;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3246b) {
            case 0:
                this.f3247c.lambda$postCheckRequirements$0();
                break;
            default:
                this.f3247c.lambda$postRecheckNotMetNetworkRequirements$1();
                break;
        }
    }
}
