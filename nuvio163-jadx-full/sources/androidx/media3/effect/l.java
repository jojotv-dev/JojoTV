package androidx.media3.effect;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class l implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f2879b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ DefaultVideoFrameProcessor f2880c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ InterruptedException f2881e;

    public /* synthetic */ l(DefaultVideoFrameProcessor defaultVideoFrameProcessor, InterruptedException interruptedException, int i10) {
        this.f2879b = i10;
        this.f2880c = defaultVideoFrameProcessor;
        this.f2881e = interruptedException;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f2879b) {
            case 0:
                this.f2880c.lambda$registerInputStream$1(this.f2881e);
                break;
            default:
                this.f2880c.lambda$flush$4(this.f2881e);
                break;
        }
    }
}
