package androidx.media3.exoplayer.video;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class h implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3378b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ VideoSink.Listener f3379c;

    public /* synthetic */ h(VideoSink.Listener listener, int i10) {
        this.f3378b = i10;
        this.f3379c = listener;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3378b) {
            case 0:
                this.f3379c.onFrameDropped();
                break;
            case 1:
                this.f3379c.onFirstFrameRendered();
                break;
            default:
                this.f3379c.onFrameAvailableForRendering();
                break;
        }
    }
}
