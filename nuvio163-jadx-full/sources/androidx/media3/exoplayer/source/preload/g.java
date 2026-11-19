package androidx.media3.exoplayer.source.preload;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class g implements q7.i {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3302b;

    public /* synthetic */ g(int i10) {
        this.f3302b = i10;
    }

    @Override // q7.i
    public final boolean apply(Object obj) {
        DefaultPreloadManager.PreloadStatus preloadStatus = (DefaultPreloadManager.PreloadStatus) obj;
        switch (this.f3302b) {
            case 0:
                return DefaultPreloadManager.SourcePreloadControl.lambda$onTracksSelected$1(preloadStatus);
            default:
                return DefaultPreloadManager.SourcePreloadControl.lambda$onSourcePrepared$0(preloadStatus);
        }
    }
}
