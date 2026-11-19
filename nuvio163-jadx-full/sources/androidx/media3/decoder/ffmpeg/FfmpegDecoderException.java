package androidx.media3.decoder.ffmpeg;

import androidx.media3.common.util.UnstableApi;
import androidx.media3.decoder.DecoderException;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public final class FfmpegDecoderException extends DecoderException {
    public FfmpegDecoderException(String str) {
        super(str);
    }

    public FfmpegDecoderException(String str, Throwable th2) {
        super(str, th2);
    }
}
