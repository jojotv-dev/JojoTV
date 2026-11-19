package androidx.media3.extractor.text;

import androidx.media3.common.util.UnstableApi;
import androidx.media3.decoder.DecoderException;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public class SubtitleDecoderException extends DecoderException {
    public SubtitleDecoderException(String str) {
        super(str);
    }

    public SubtitleDecoderException(Throwable th2) {
        super(th2);
    }

    public SubtitleDecoderException(String str, Throwable th2) {
        super(str, th2);
    }
}
