package androidx.media3.session;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class l implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3576b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ MediaControllerHolder f3577c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ MediaController f3578e;

    public /* synthetic */ l(MediaControllerHolder mediaControllerHolder, MediaController mediaController, int i10) {
        this.f3576b = i10;
        this.f3577c = mediaControllerHolder;
        this.f3578e = mediaController;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3576b) {
            case 0:
                this.f3577c.setController(this.f3578e);
                break;
            default:
                this.f3577c.lambda$setController$0(this.f3578e);
                break;
        }
    }
}
