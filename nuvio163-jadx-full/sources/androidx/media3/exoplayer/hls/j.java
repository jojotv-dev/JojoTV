package androidx.media3.exoplayer.hls;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class j implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3157b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f3158c;

    public /* synthetic */ j(Object obj, int i10) {
        this.f3157b = i10;
        this.f3158c = obj;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3157b) {
            case 0:
                ((HlsSampleStreamWrapper) this.f3158c).maybeFinishPrepare();
                break;
            case 1:
                ((HlsSampleStreamWrapper) this.f3158c).onTracksEnded();
                break;
            default:
                ((HlsSampleStreamWrapper.Callback) this.f3158c).onPrepared();
                break;
        }
    }
}
