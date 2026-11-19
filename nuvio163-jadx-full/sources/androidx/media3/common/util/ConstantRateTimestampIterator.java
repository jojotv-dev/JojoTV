package androidx.media3.common.util;

import androidx.media3.common.C;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public final class ConstantRateTimestampIterator implements TimestampIterator {
    private final long endPositionUs;
    private final float frameRate;
    private int framesAdded;
    private final double framesDurationUs;
    private final long startPositionUs;
    private final int totalNumberOfFramesToAdd;

    public ConstantRateTimestampIterator(long j10, float f10) {
        this(0L, j10, f10);
    }

    private long getTimestampUsAfter(int i10) {
        long jRound = Math.round(this.framesDurationUs * ((double) i10)) + this.startPositionUs;
        Assertions.checkState(jRound >= 0);
        return jRound;
    }

    @Override // androidx.media3.common.util.TimestampIterator
    public long getLastTimestampUs() {
        int i10 = this.totalNumberOfFramesToAdd;
        return i10 == 0 ? C.TIME_UNSET : getTimestampUsAfter(i10 - 1);
    }

    @Override // androidx.media3.common.util.TimestampIterator
    public boolean hasNext() {
        return this.framesAdded < this.totalNumberOfFramesToAdd;
    }

    @Override // androidx.media3.common.util.TimestampIterator
    public long next() {
        Assertions.checkState(hasNext());
        int i10 = this.framesAdded;
        this.framesAdded = i10 + 1;
        return getTimestampUsAfter(i10);
    }

    public ConstantRateTimestampIterator(long j10, long j11, float f10) {
        boolean z2 = false;
        Assertions.checkArgument(j11 > 0);
        Assertions.checkArgument(f10 > 0.0f);
        if (0 <= j10 && j10 < j11) {
            z2 = true;
        }
        Assertions.checkArgument(z2);
        this.startPositionUs = j10;
        this.endPositionUs = j11;
        this.frameRate = f10;
        this.totalNumberOfFramesToAdd = Math.max(Math.round(((j11 - j10) / 1000000.0f) * f10), 1);
        this.framesDurationUs = 1000000.0f / f10;
    }

    @Override // androidx.media3.common.util.TimestampIterator
    public ConstantRateTimestampIterator copyOf() {
        return new ConstantRateTimestampIterator(this.startPositionUs, this.endPositionUs, this.frameRate);
    }
}
