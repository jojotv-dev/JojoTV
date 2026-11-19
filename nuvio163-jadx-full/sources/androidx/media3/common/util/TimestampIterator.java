package androidx.media3.common.util;

import androidx.media3.common.C;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public interface TimestampIterator {
    TimestampIterator copyOf();

    default long getLastTimestampUs() {
        return C.TIME_UNSET;
    }

    boolean hasNext();

    long next();
}
