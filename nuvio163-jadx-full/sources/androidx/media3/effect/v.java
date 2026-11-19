package androidx.media3.effect;

import androidx.media3.common.util.GlUtil;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class v implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f2907b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Exception f2908c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ long f2909e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ GlShaderProgram f2910i;

    public /* synthetic */ v(GlShaderProgram glShaderProgram, Exception exc, long j10, int i10) {
        this.f2907b = i10;
        this.f2910i = glShaderProgram;
        this.f2908c = exc;
        this.f2909e = j10;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f2907b) {
            case 0:
                ((FinalShaderProgramWrapper) this.f2910i).lambda$renderFrame$6(this.f2908c, this.f2909e);
                break;
            case 1:
                ((DebugViewShaderProgram) this.f2910i).lambda$queueInputFrame$2(this.f2908c, this.f2909e);
                break;
            default:
                ((SeparableConvolutionShaderProgram) this.f2910i).lambda$queueInputFrame$1((GlUtil.GlException) this.f2908c, this.f2909e);
                break;
        }
    }
}
