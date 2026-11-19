package androidx.media3.exoplayer.source.ads;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class a implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3255b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ AdsMediaSource f3256c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ AdsMediaSource.ComponentListener f3257e;

    public /* synthetic */ a(AdsMediaSource adsMediaSource, AdsMediaSource.ComponentListener componentListener, int i10) {
        this.f3255b = i10;
        this.f3256c = adsMediaSource;
        this.f3257e = componentListener;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3255b) {
            case 0:
                this.f3256c.lambda$prepareSourceInternal$0(this.f3257e);
                break;
            default:
                this.f3256c.lambda$releaseSourceInternal$1(this.f3257e);
                break;
        }
    }
}
