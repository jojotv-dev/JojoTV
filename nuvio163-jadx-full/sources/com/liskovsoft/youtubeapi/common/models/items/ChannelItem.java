package com.liskovsoft.youtubeapi.common.models.items;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import com.liskovsoft.googlecommon.common.models.items.Thumbnail;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class ChannelItem {

    @JsonPath({"$.channelId", "$.navigationEndpoint.browseEndpoint.browseId"})
    private String mChannelId;

    @JsonPath({"$.subscriberCountText.simpleText", "$.subscriberCountText.runs[0].text"})
    private String mSubscriberCountText;

    @JsonPath({"$.thumbnail.thumbnails[*]"})
    private List<Thumbnail> mThumbnails;

    @JsonPath({"$.title.simpleText", "$.title.runs[0].text", "$.displayName.runs[0].text"})
    private String mTitle;

    @JsonPath({"$.videoCountText.runs[0].text"})
    private String mVideoCount1;

    @JsonPath({"$.videoCountText.runs[1].text"})
    private String mVideoCount2;

    public String getChannelId() {
        return this.mChannelId;
    }

    public String getSubscriberCountText() {
        return this.mSubscriberCountText;
    }

    public List<Thumbnail> getThumbnails() {
        return this.mThumbnails;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getVideoCountText() {
        if (this.mVideoCount2 == null) {
            return this.mVideoCount1;
        }
        return this.mVideoCount1 + this.mVideoCount2;
    }
}
