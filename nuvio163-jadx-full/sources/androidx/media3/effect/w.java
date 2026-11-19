package androidx.media3.effect;

import androidx.media3.common.VideoFrameProcessingException;
import androidx.media3.common.util.GlUtil;
import androidx.media3.common.util.Size;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class w implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f2911b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f2912c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Object f2913e;

    public /* synthetic */ w(Object obj, Object obj2, int i10) {
        this.f2911b = i10;
        this.f2912c = obj;
        this.f2913e = obj2;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f2911b) {
            case 0:
                ((FinalShaderProgramWrapper) this.f2912c).lambda$ensureConfigured$7((Size) this.f2913e);
                break;
            case 1:
                ((FinalShaderProgramWrapper) this.f2912c).lambda$setOutputSurfaceInfo$4((InterruptedException) this.f2913e);
                break;
            case 2:
                ((FinalShaderProgramWrapper) this.f2912c).lambda$destroyOutputEglSurface$5((GlUtil.GlException) this.f2913e);
                break;
            case 3:
                ((QueuingGlShaderProgram) this.f2912c).lambda$onError$1((Exception) this.f2913e);
                break;
            case 4:
                ((SingleInputVideoGraph.AnonymousClass1) this.f2912c).lambda$onError$3((VideoFrameProcessingException) this.f2913e);
                break;
            case 5:
                ((VideoFrameProcessingTaskExecutor) this.f2912c).lambda$invoke$0((VideoFrameProcessingTaskExecutor.Task) this.f2913e);
                break;
            case 6:
                ((MultipleInputVideoGraph) this.f2912c).lambda$handleVideoFrameProcessingException$2((Exception) this.f2913e);
                break;
            default:
                ((PassthroughShaderProgram) this.f2912c).lambda$onError$1((Exception) this.f2913e);
                break;
        }
    }
}
