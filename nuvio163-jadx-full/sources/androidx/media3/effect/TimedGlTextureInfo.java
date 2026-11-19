package androidx.media3.effect;

import androidx.media3.common.GlTextureInfo;

/* JADX INFO: loaded from: classes3.dex */
final class TimedGlTextureInfo {
    public final GlTextureInfo glTextureInfo;
    public final long presentationTimeUs;

    public TimedGlTextureInfo(GlTextureInfo glTextureInfo, long j10) {
        this.glTextureInfo = glTextureInfo;
        this.presentationTimeUs = j10;
    }
}
