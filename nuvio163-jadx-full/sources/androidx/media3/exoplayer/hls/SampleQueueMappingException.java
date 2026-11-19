package androidx.media3.exoplayer.hls;

import androidx.media3.common.util.UnstableApi;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public final class SampleQueueMappingException extends IOException {
    public SampleQueueMappingException(String str) {
        super(a0.i.n("Unable to bind a sample queue to TrackGroup with MIME type ", str, "."));
    }
}
