package androidx.media3.extractor.mp4;

import androidx.media3.common.util.UnstableApi;
import androidx.media3.extractor.SniffFailure;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public final class AtomSizeTooSmallSniffFailure implements SniffFailure {
    public final long atomSize;
    public final int atomType;
    public final int minimumHeaderSize;

    public AtomSizeTooSmallSniffFailure(int i10, long j10, int i11) {
        this.atomType = i10;
        this.atomSize = j10;
        this.minimumHeaderSize = i11;
    }
}
