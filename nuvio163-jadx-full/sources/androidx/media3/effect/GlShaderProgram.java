package androidx.media3.effect;

import androidx.media3.common.GlObjectsProvider;
import androidx.media3.common.GlTextureInfo;
import androidx.media3.common.VideoFrameProcessingException;
import androidx.media3.common.util.UnstableApi;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public interface GlShaderProgram {

    public interface ErrorListener {
        void onError(VideoFrameProcessingException videoFrameProcessingException);
    }

    public interface InputListener {
        default void onFlush() {
        }

        default void onInputFrameProcessed(GlTextureInfo glTextureInfo) {
        }

        default void onReadyToAcceptInputFrame() {
        }
    }

    public interface OutputListener {
        default void onCurrentOutputStreamEnded() {
        }

        default void onOutputFrameAvailable(GlTextureInfo glTextureInfo, long j10) {
        }
    }

    void flush();

    void queueInputFrame(GlObjectsProvider glObjectsProvider, GlTextureInfo glTextureInfo, long j10);

    void release();

    void releaseOutputFrame(GlTextureInfo glTextureInfo);

    void setErrorListener(Executor executor, ErrorListener errorListener);

    void setInputListener(InputListener inputListener);

    void setOutputListener(OutputListener outputListener);

    void signalEndOfCurrentInputStream();
}
