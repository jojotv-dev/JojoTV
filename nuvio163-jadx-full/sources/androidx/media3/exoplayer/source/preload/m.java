package androidx.media3.exoplayer.source.preload;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class m implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3316b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ PreCacheHelper.DownloadCallback f3317c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ PreCacheHelper.Task f3318e;

    public /* synthetic */ m(PreCacheHelper.DownloadCallback downloadCallback, PreCacheHelper.Task task, int i10) {
        this.f3316b = i10;
        this.f3317c = downloadCallback;
        this.f3318e = task;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3316b) {
            case 0:
                this.f3317c.lambda$onDownloadProgress$5(this.f3318e);
                break;
            default:
                this.f3317c.lambda$onDownloadStopped$3(this.f3318e);
                break;
        }
    }
}
