package androidx.media3.extractor.metadata;

import androidx.media3.common.Metadata;
import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public interface MetadataDecoder {
    Metadata decode(MetadataInputBuffer metadataInputBuffer);
}
