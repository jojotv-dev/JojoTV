package androidx.media3.effect;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class j implements VideoFrameProcessingTaskExecutor.Task {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f2876a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ DefaultVideoCompositor f2877b;

    public /* synthetic */ j(DefaultVideoCompositor defaultVideoCompositor, int i10) {
        this.f2876a = i10;
        this.f2877b = defaultVideoCompositor;
    }

    @Override // androidx.media3.effect.VideoFrameProcessingTaskExecutor.Task
    public final void run() {
        switch (this.f2876a) {
            case 0:
                this.f2877b.releaseGlObjects();
                break;
            case 1:
                this.f2877b.maybeComposite();
                break;
            default:
                this.f2877b.setupGlObjects();
                break;
        }
    }
}
