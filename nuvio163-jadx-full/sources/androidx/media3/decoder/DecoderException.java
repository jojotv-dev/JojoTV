package androidx.media3.decoder;

import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public class DecoderException extends Exception {
    public DecoderException(String str) {
        super(str);
    }

    public DecoderException(Throwable th2) {
        super(th2);
    }

    public DecoderException(String str, Throwable th2) {
        super(str, th2);
    }
}
