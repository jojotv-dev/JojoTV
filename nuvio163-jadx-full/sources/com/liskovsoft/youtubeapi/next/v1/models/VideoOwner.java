package com.liskovsoft.youtubeapi.next.v1.models;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import com.liskovsoft.googlecommon.common.models.items.Thumbnail;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class VideoOwner {

    @JsonPath({"$.navigationEndpoint.browseEndpoint.browseId", "$.subscribeButton.subscribeButtonRenderer.channelId"})
    private String mChannelId;

    @JsonPath({"$.subscriptionButton.subscribed", "$.subscribed", "$.subscribeButton.subscribeButtonRenderer.subscribed"})
    private Boolean mIsSubscribed;

    @JsonPath({"$.subscriberCountText.simpleText", "$.subscriberCountText.runs[0].text", "$.subscribeButton.subscribeButtonRenderer.subscriberCountText.runs[0].text"})
    private String mSubscriberCount;

    @JsonPath({"$.longSubscriberCountText.runs[0].text", "$.subscribeButton.subscribeButtonRenderer.longSubscriberCountText.runs[0].text"})
    private String mSubscriberCountLong;

    @JsonPath({"$.shortSubscriberCountText.runs[0].text", "$.subscribeButton.subscribeButtonRenderer.shortSubscriberCountText.runs[0].text"})
    private String mSubscriberCountShort;

    @JsonPath({"$.thumbnail.thumbnails[*]"})
    private List<Thumbnail> mThumbnails;

    @JsonPath({"$.title.simpleText", "$.title.runs[0].text"})
    private String mVideoAuthor;

    public String getChannelId() {
        return this.mChannelId;
    }

    public String getSubscriberCount() {
        return this.mSubscriberCount;
    }

    public String getSubscriberCountLong() {
        return this.mSubscriberCountLong;
    }

    public String getSubscriberCountShort() {
        return this.mSubscriberCountShort;
    }

    public List<Thumbnail> getThumbnails() {
        return this.mThumbnails;
    }

    public String getVideoAuthor() {
        return this.mVideoAuthor;
    }

    public Boolean isSubscribed() {
        return this.mIsSubscribed;
    }
}
