package com.liskovsoft.youtubeapi.videoinfo.models;

import com.liskovsoft.googlecommon.common.converters.regexp.RegExp;

/* JADX INFO: loaded from: classes4.dex */
public class DashInfoContent extends DashInfoBase {

    @RegExp({"Sequence-Number: (\\d*)"})
    private String mLastSegmentNumStr;

    @RegExp({"Ingestion-Walltime-Us: (\\d*)"})
    private String mLastSegmentTimeUs;

    @RegExp({"Stream-Duration-Us: (\\d*)"})
    private String mStreamDurationUs;
    private int mLastSegmentNum = -1;
    private long mLastSegmentTimeMs = -1;
    private long mStreamDurationMs = -1;

    @Override // com.liskovsoft.youtubeapi.videoinfo.models.DashInfoBase
    public int getLastSegmentNum() {
        if (this.mLastSegmentNum == -1) {
            this.mLastSegmentNum = Integer.parseInt(this.mLastSegmentNumStr);
        }
        return this.mLastSegmentNum;
    }

    @Override // com.liskovsoft.youtubeapi.videoinfo.models.DashInfoBase
    public long getLastSegmentTimeMs() {
        if (this.mLastSegmentTimeMs == -1) {
            this.mLastSegmentTimeMs = Long.parseLong(this.mLastSegmentTimeUs) / 1000;
        }
        return this.mLastSegmentTimeMs;
    }

    @Override // com.liskovsoft.youtubeapi.videoinfo.models.DashInfoBase
    public long getStreamDurationMs() {
        if (this.mStreamDurationMs == -1) {
            this.mStreamDurationMs = Long.parseLong(this.mStreamDurationUs) / 1000;
        }
        return this.mStreamDurationMs;
    }
}
