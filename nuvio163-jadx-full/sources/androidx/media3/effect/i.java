package androidx.media3.effect;

import androidx.media3.common.util.GlUtil;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class i implements VideoFrameProcessingTaskExecutor.Task {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f2873a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ long f2874b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f2875c;

    public /* synthetic */ i(Object obj, long j10, int i10) {
        this.f2873a = i10;
        this.f2875c = obj;
        this.f2874b = j10;
    }

    @Override // androidx.media3.effect.VideoFrameProcessingTaskExecutor.Task
    public final void run() throws GlUtil.GlException {
        switch (this.f2873a) {
            case 0:
                ((DefaultVideoCompositor) this.f2875c).lambda$releaseOutputTexture$0(this.f2874b);
                break;
            case 1:
                ((DefaultVideoFrameProcessor) this.f2875c).lambda$renderOutputFrame$3(this.f2874b);
                break;
            default:
                ((FinalShaderProgramWrapper) this.f2875c).lambda$releaseOutputTexture$0(this.f2874b);
                break;
        }
    }
}
