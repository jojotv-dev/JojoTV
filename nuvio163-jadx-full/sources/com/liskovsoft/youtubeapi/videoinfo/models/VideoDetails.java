package com.liskovsoft.youtubeapi.videoinfo.models;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import com.liskovsoft.googlecommon.common.models.items.Thumbnail;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class VideoDetails {

    @JsonPath({"$.author"})
    private String mAuthor;

    @JsonPath({"$.channelId"})
    private String mChannelId;

    @JsonPath({"$.isLive"})
    private boolean mIsLive;

    @JsonPath({"$.isLiveContent"})
    private boolean mIsLiveContent;

    @JsonPath({"$.isLiveDvrEnabled"})
    private boolean mIsLiveDvrEnabled;

    @JsonPath({"$.isLowLatencyLiveStream"})
    private boolean mIsLowLatencyLiveStream;

    @JsonPath({"$.isOwnerViewing"})
    private boolean mIsOwnerViewing;

    @JsonPath({"$.lengthSeconds"})
    private String mLengthSeconds;

    @JsonPath({"$.shortDescription"})
    private String mShortDescription;

    @JsonPath({"$.thumbnail.thumbnails[*]"})
    private List<Thumbnail> mThumbnails;

    @JsonPath({"$.title"})
    private String mTitle;

    @JsonPath({"$.videoId"})
    private String mVideoId;

    @JsonPath({"$.viewCount"})
    private String mViewCount;

    public String getAuthor() {
        return this.mAuthor;
    }

    public String getChannelId() {
        return this.mChannelId;
    }

    public String getLengthSeconds() {
        return this.mLengthSeconds;
    }

    public String getShortDescription() {
        return this.mShortDescription;
    }

    public List<Thumbnail> getThumbnails() {
        return this.mThumbnails;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getVideoId() {
        return this.mVideoId;
    }

    public String getViewCount() {
        return this.mViewCount;
    }

    public boolean isLive() {
        return this.mIsLive;
    }

    public boolean isLiveContent() {
        return this.mIsLiveContent;
    }

    public boolean isLiveDvrEnabled() {
        return this.mIsLiveDvrEnabled;
    }

    public boolean isLowLatencyLiveStream() {
        return this.mIsLowLatencyLiveStream;
    }

    public boolean isOwnerViewing() {
        return this.mIsOwnerViewing;
    }
}
