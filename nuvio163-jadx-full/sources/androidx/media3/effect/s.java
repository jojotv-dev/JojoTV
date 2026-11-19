package androidx.media3.effect;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class s implements VideoFrameProcessingTaskExecutor.Task {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f2901a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ ExternalTextureManager f2902b;

    public /* synthetic */ s(ExternalTextureManager externalTextureManager, int i10) {
        this.f2901a = i10;
        this.f2902b = externalTextureManager;
    }

    @Override // androidx.media3.effect.VideoFrameProcessingTaskExecutor.Task
    public final void run() {
        switch (this.f2901a) {
            case 0:
                this.f2902b.lambda$signalEndOfCurrentInputStream$6();
                break;
            case 1:
                this.f2902b.lambda$registerInputFrame$5();
                break;
            case 2:
                this.f2902b.lambda$releaseAllRegisteredFrames$7();
                break;
            case 3:
                this.f2902b.lambda$onInputFrameProcessed$4();
                break;
            case 4:
                this.f2902b.forceSignalEndOfStream();
                break;
            case 5:
                this.f2902b.lambda$new$0();
                break;
            default:
                this.f2902b.lambda$onReadyToAcceptInputFrame$3();
                break;
        }
    }
}
