package com.liskovsoft.youtubeapi.videoinfo.models;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;

/* JADX INFO: loaded from: classes4.dex */
public class VideoInfoHls {

    @JsonPath({"$.streamingData.dashManifestUrl"})
    private String mDashManifestUrl;

    @JsonPath({"$.streamingData.hlsManifestUrl"})
    private String mHlsManifestUrl;

    @JsonPath({"$.storyboards.playerStoryboardSpecRenderer.spec"})
    private String mStoryboardSpec;

    public String getDashManifestUrl() {
        return this.mDashManifestUrl;
    }

    public String getHlsManifestUrl() {
        return this.mHlsManifestUrl;
    }

    public String getStoryboardSpec() {
        return this.mStoryboardSpec;
    }
}
