package androidx.media3.extractor.mkv;

import androidx.media3.extractor.ExtractorInput;

/* JADX INFO: loaded from: classes3.dex */
interface EbmlReader {
    void init(EbmlProcessor ebmlProcessor);

    boolean read(ExtractorInput extractorInput);

    void reset();
}
