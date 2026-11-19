package com.liskovsoft.youtubeapi.next.v1.models;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import com.liskovsoft.googlecommon.common.models.items.Thumbnail;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class ButtonStates {

    @JsonPath({"$.channelButton.videoOwnerRenderer.navigationEndpoint.browseEndpoint.browseId"})
    private String mChannelId;

    @JsonPath({"$.channelButton.videoOwnerRenderer.thumbnail.thumbnails[*]"})
    private List<Thumbnail> mChannelThumbnails;

    @JsonPath({"$.dislikeButton.toggleButtonRenderer.isToggled"})
    private Boolean mIsDislikeToggled;

    @JsonPath({"$.likeButton.toggleButtonRenderer.isToggled"})
    private Boolean mIsLikeToggled;

    @JsonPath({"$.subscribeButton.toggleButtonRenderer.isToggled"})
    private Boolean mIsSubscribeToggled;

    public String getChannelId() {
        return this.mChannelId;
    }

    public List<Thumbnail> getChannelThumbnails() {
        return this.mChannelThumbnails;
    }

    public Boolean isDislikeToggled() {
        return this.mIsDislikeToggled;
    }

    public Boolean isLikeToggled() {
        return this.mIsLikeToggled;
    }

    public Boolean isSubscribeToggled() {
        return this.mIsSubscribeToggled;
    }
}
