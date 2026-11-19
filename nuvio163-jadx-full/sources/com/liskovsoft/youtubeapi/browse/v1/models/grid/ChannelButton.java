package com.liskovsoft.youtubeapi.browse.v1.models.grid;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;

/* JADX INFO: loaded from: classes4.dex */
public class ChannelButton {

    @JsonPath({"$.navigationEndpoint.browseEndpoint.browseId"})
    private String mBrowseId;

    @JsonPath({"$.text.simpleText"})
    private String mButtonText;

    @JsonPath({"$.navigationEndpoint.browseEndpoint.canonicalBaseUrl"})
    private String mCanonicalBaseUrl;

    @JsonPath({"$.navigationEndpoint.watchEndpoint.params"})
    private String mParams;

    @JsonPath({"$.navigationEndpoint.watchEndpoint.videoId"})
    private String mVideoId;

    public String getBrowseId() {
        return this.mBrowseId;
    }

    public String getButtonText() {
        return this.mButtonText;
    }

    public String getCanonicalBaseUrl() {
        return this.mCanonicalBaseUrl;
    }

    public String getParams() {
        return this.mParams;
    }

    public String getVideoId() {
        return this.mVideoId;
    }
}
