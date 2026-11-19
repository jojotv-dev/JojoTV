package androidx.media3.decoder;

import androidx.media3.common.util.UnstableApi;
import androidx.media3.decoder.DecoderException;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public interface Decoder<I, O, E extends DecoderException> {
    I dequeueInputBuffer();

    O dequeueOutputBuffer();

    void flush();

    String getName();

    void queueInputBuffer(I i10);

    void release();

    void setOutputStartTimeUs(long j10);
}
