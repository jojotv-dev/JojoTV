package androidx.media3.exoplayer.source.preload;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class o implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3321b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ PreloadMediaSource f3322c;

    public /* synthetic */ o(PreloadMediaSource preloadMediaSource, int i10) {
        this.f3321b = i10;
        this.f3322c = preloadMediaSource;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3321b) {
            case 0:
                this.f3322c.lambda$releasePreloadMediaSource$3();
                break;
            case 1:
                this.f3322c.lambda$clear$1();
                break;
            default:
                this.f3322c.checkForPreloadError();
                break;
        }
    }
}
