package androidx.media3.effect;

import android.graphics.Gainmap;
import androidx.media3.common.VideoFrameProcessingException;
import androidx.media3.common.util.Log;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class e0 implements GlShaderProgram.ErrorListener {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f2858b;

    public /* synthetic */ e0(int i10) {
        this.f2858b = i10;
    }

    public static /* bridge */ /* synthetic */ Gainmap a(Object obj) {
        return (Gainmap) obj;
    }

    @Override // androidx.media3.effect.GlShaderProgram.ErrorListener
    public void onError(VideoFrameProcessingException videoFrameProcessingException) {
        switch (this.f2858b) {
            case 0:
                Log.e(QueuingGlShaderProgram.TAG, "Exception caught by default QueuingGlShaderProgram errorListener.", videoFrameProcessingException);
                break;
            case 1:
                Log.e(BaseGlShaderProgram.TAG, "Exception caught by default BaseGlShaderProgram errorListener.", videoFrameProcessingException);
                break;
            case 2:
            default:
                SeparableConvolutionShaderProgram.lambda$new$0(videoFrameProcessingException);
                break;
            case 3:
                Log.e(DebugViewShaderProgram.TAG, "Exception caught by errorListener.", videoFrameProcessingException);
                break;
            case 4:
                PassthroughShaderProgram.lambda$new$0(videoFrameProcessingException);
                break;
        }
    }
}
