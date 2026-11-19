package androidx.media3.exoplayer.hls;

import androidx.media3.common.util.UnstableApi;
import androidx.media3.extractor.ExtractorInput;
import androidx.media3.extractor.ExtractorOutput;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public interface HlsMediaChunkExtractor {
    void init(ExtractorOutput extractorOutput);

    boolean isPackedAudioExtractor();

    boolean isReusable();

    void onTruncatedSegmentParsed();

    boolean read(ExtractorInput extractorInput);

    HlsMediaChunkExtractor recreate();
}
