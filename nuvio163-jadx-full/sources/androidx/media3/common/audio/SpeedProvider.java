package androidx.media3.common.audio;

import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public interface SpeedProvider {
    long getNextSpeedChangeTimeUs(long j10);

    float getSpeed(long j10);
}
