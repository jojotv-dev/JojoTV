package com.liskovsoft.youtubeapi.videoinfo.models;

import kotlin.time.DurationKt;

/* JADX INFO: loaded from: classes4.dex */
public abstract class DashInfoBase implements DashInfo {
    private static final int MAX_DURATION_MS = 86400000;
    private int mSegmentDurationUs = -1;
    private int mStartSegmentNum = -1;
    private long mStartTimeMs = -1;

    public abstract int getLastSegmentNum();

    public abstract long getLastSegmentTimeMs();

    @Override // com.liskovsoft.youtubeapi.videoinfo.models.DashInfo
    public int getSegmentDurationUs() {
        if (this.mSegmentDurationUs == -1) {
            this.mSegmentDurationUs = (int) ((getStreamDurationMs() / getLastSegmentNum()) * 1000.0f);
        }
        return this.mSegmentDurationUs;
    }

    @Override // com.liskovsoft.youtubeapi.videoinfo.models.DashInfo
    public int getStartSegmentNum() {
        if (this.mStartSegmentNum == -1) {
            this.mStartSegmentNum = Math.max(getLastSegmentNum() - ((int) (86400000000L / ((long) getSegmentDurationUs()))), 0);
        }
        return this.mStartSegmentNum;
    }

    @Override // com.liskovsoft.youtubeapi.videoinfo.models.DashInfo
    public long getStartTimeMs() {
        return System.currentTimeMillis() - Math.min(getStreamDurationMs(), DurationKt.MILLIS_IN_DAY);
    }

    public abstract long getStreamDurationMs();

    @Override // com.liskovsoft.youtubeapi.videoinfo.models.DashInfo
    public boolean isSeekable() {
        return true;
    }
}
