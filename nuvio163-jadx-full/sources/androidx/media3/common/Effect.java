package androidx.media3.common;

import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public interface Effect {
    default long getDurationAfterEffectApplied(long j10) {
        return j10;
    }
}
