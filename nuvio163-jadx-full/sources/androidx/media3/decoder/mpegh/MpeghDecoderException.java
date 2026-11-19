package androidx.media3.decoder.mpegh;

import androidx.media3.common.util.UnstableApi;
import androidx.media3.decoder.DecoderException;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public class MpeghDecoderException extends DecoderException {
    public MpeghDecoderException(String str) {
        super(str, new Throwable());
    }

    public MpeghDecoderException(String str, Throwable th2) {
        super(str, th2);
    }
}
