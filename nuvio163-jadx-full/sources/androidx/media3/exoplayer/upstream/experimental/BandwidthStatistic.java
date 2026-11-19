package androidx.media3.exoplayer.upstream.experimental;

import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public interface BandwidthStatistic {
    void addSample(long j10, long j11);

    long getBandwidthEstimate();

    void reset();
}
