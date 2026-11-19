package androidx.media3.effect;

import androidx.media3.common.VideoFrameProcessingException;
import androidx.media3.common.util.GlUtil;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class b implements VideoFrameProcessingTaskExecutor.Task {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f2836a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ BitmapTextureManager f2837b;

    public /* synthetic */ b(BitmapTextureManager bitmapTextureManager, int i10) {
        this.f2836a = i10;
        this.f2837b = bitmapTextureManager;
    }

    @Override // androidx.media3.effect.VideoFrameProcessingTaskExecutor.Task
    public final void run() throws VideoFrameProcessingException, GlUtil.GlException {
        switch (this.f2836a) {
            case 0:
                this.f2837b.lambda$onReadyToAcceptInputFrame$0();
                break;
            case 1:
                this.f2837b.lambda$signalEndOfCurrentInputStream$2();
                break;
            default:
                this.f2837b.lambda$release$3();
                break;
        }
    }
}
