package com.liskovsoft.youtubeapi.browse.v1.models.guide;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;

/* JADX INFO: loaded from: classes4.dex */
public class GuideItem {

    @JsonPath({"$.navigationEndpoint.browseEndpoint.browseId"})
    private String mBrowseId;

    @JsonPath({"$.icon.iconType"})
    private String mIconType;

    @JsonPath({"$.navigationEndpoint.browseEndpoint.params"})
    private String mParams;

    @JsonPath({"$.title"})
    private String mTitle;

    @JsonPath({"$.formattedTitle.simpleText"})
    private String mTitleAlt;

    public String getBrowseId() {
        return this.mBrowseId;
    }

    public String getIconType() {
        return this.mIconType;
    }

    public String getParams() {
        return this.mParams;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getTitleAlt() {
        return this.mTitleAlt;
    }
}
