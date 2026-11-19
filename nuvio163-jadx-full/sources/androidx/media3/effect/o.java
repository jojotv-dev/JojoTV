package androidx.media3.effect;

import androidx.media3.common.VideoFrameProcessingException;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class o implements VideoFrameProcessingTaskExecutor.Task {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f2887a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ DefaultVideoFrameProcessor f2888b;

    public /* synthetic */ o(DefaultVideoFrameProcessor defaultVideoFrameProcessor, int i10) {
        this.f2887a = i10;
        this.f2888b = defaultVideoFrameProcessor;
    }

    @Override // androidx.media3.effect.VideoFrameProcessingTaskExecutor.Task
    public final void run() throws VideoFrameProcessingException {
        switch (this.f2887a) {
            case 0:
                this.f2888b.configurePendingInputStream();
                break;
            case 1:
                this.f2888b.releaseGlObjects();
                break;
            case 2:
                this.f2888b.lambda$redraw$0();
                break;
            default:
                DefaultVideoFrameProcessor.access$1300(this.f2888b);
                break;
        }
    }
}
