package com.liskovsoft.youtubeapi.service.data;

import com.liskovsoft.mediaserviceinterfaces.data.SponsorSegment;
import com.liskovsoft.youtubeapi.block.data.Segment;
import com.liskovsoft.youtubeapi.block.data.SegmentList;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class YouTubeSponsorSegment implements SponsorSegment {
    private String mAction;
    private String mCategory;
    private long mEndMs;
    private long mStartMs;

    public static List<SponsorSegment> from(SegmentList segmentList) {
        if (segmentList == null || segmentList.getSegments() == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (Segment segment : segmentList.getSegments()) {
            YouTubeSponsorSegment youTubeSponsorSegment = new YouTubeSponsorSegment();
            youTubeSponsorSegment.mStartMs = (long) (segment.getStart() * 1000.0f);
            youTubeSponsorSegment.mEndMs = (long) (segment.getEnd() * 1000.0f);
            youTubeSponsorSegment.mCategory = segment.getCategory();
            youTubeSponsorSegment.mAction = segment.getActionType();
            arrayList.add(youTubeSponsorSegment);
        }
        return arrayList;
    }

    @Override // com.liskovsoft.mediaserviceinterfaces.data.SponsorSegment
    public String getAction() {
        return this.mAction;
    }

    @Override // com.liskovsoft.mediaserviceinterfaces.data.SponsorSegment
    public String getCategory() {
        return this.mCategory;
    }

    @Override // com.liskovsoft.mediaserviceinterfaces.data.SponsorSegment
    public long getEndMs() {
        return this.mEndMs;
    }

    @Override // com.liskovsoft.mediaserviceinterfaces.data.SponsorSegment
    public long getStartMs() {
        return this.mStartMs;
    }
}
