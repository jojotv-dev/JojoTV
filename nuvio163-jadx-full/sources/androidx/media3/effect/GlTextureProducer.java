package androidx.media3.effect;

import androidx.media3.common.GlTextureInfo;
import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public interface GlTextureProducer {

    public interface Listener {
        void onTextureRendered(GlTextureProducer glTextureProducer, GlTextureInfo glTextureInfo, long j10, long j11);
    }

    void releaseOutputTexture(long j10);
}
