package androidx.media3.exoplayer.video;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class d implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3369b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ DefaultVideoSink.FrameRendererImpl f3370c;

    public /* synthetic */ d(DefaultVideoSink.FrameRendererImpl frameRendererImpl, int i10) {
        this.f3369b = i10;
        this.f3370c = frameRendererImpl;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3369b) {
            case 0:
                this.f3370c.lambda$renderFrame$1();
                break;
            default:
                this.f3370c.lambda$dropFrame$2();
                break;
        }
    }
}
