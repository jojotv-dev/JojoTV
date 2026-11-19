package androidx.media3.exoplayer.source.preload;

import androidx.media3.exoplayer.source.MediaPeriod;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class q implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3325b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ PreloadMediaSource.PreloadMediaPeriodCallback f3326c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ MediaPeriod f3327e;

    public /* synthetic */ q(PreloadMediaSource.PreloadMediaPeriodCallback preloadMediaPeriodCallback, MediaPeriod mediaPeriod, int i10) {
        this.f3325b = i10;
        this.f3326c = preloadMediaPeriodCallback;
        this.f3327e = mediaPeriod;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3325b) {
            case 0:
                this.f3326c.lambda$onPrepared$0(this.f3327e);
                break;
            default:
                this.f3326c.lambda$onContinueLoadingRequested$1(this.f3327e);
                break;
        }
    }
}
