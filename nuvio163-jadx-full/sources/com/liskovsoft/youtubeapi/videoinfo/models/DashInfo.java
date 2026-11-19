package com.liskovsoft.youtubeapi.videoinfo.models;

/* JADX INFO: loaded from: classes4.dex */
public interface DashInfo {
    int getSegmentDurationUs();

    int getStartSegmentNum();

    long getStartTimeMs();

    boolean isSeekable();
}
