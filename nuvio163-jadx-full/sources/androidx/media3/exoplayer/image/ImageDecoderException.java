package androidx.media3.exoplayer.image;

import androidx.media3.common.util.UnstableApi;
import androidx.media3.decoder.DecoderException;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public final class ImageDecoderException extends DecoderException {
    public ImageDecoderException(String str) {
        super(str);
    }

    public ImageDecoderException(Throwable th2) {
        super(th2);
    }

    public ImageDecoderException(String str, Throwable th2) {
        super(str, th2);
    }
}
