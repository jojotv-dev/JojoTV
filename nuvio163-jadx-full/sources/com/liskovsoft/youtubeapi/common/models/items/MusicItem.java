package com.liskovsoft.youtubeapi.common.models.items;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import com.liskovsoft.googlecommon.common.helpers.YouTubeHelper;
import com.liskovsoft.googlecommon.common.models.items.Thumbnail;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class MusicItem {

    @JsonPath({"$.lengthText.accessibility.accessibilityData.label"})
    private String mAccessibilityLength;

    @JsonPath({"$.menu.menuRenderer.items[0].menuNavigationItemRenderer.navigationEndpoint.browseEndpoint.browseId"})
    private String mChannelId;

    @JsonPath({"$.lengthText.simpleText", "$.lengthText.runs[0].text"})
    private String mLengthText;

    @JsonPath({"$.thumbnailOverlays[0].thumbnailOverlayResumePlaybackRenderer.percentDurationWatched"})
    private int mPercentWatched;

    @JsonPath({"$.navigationEndpoint.watchEndpoint.playlistId"})
    private String mPlaylistId;

    @JsonPath({"$.navigationEndpoint.watchEndpoint.index"})
    private int mPlaylistIndex = -1;

    @JsonPath({"$.tertiaryText.runs[2].text"})
    private String mPublishedText;

    @JsonPath({"$.richThumbnail.movingThumbnailRenderer.movingThumbnailDetails.thumbnails[0].url"})
    private String mRichThumbnailUrl;

    @JsonPath({"$.thumbnail.thumbnails[*]"})
    private List<Thumbnail> mThumbnails;

    @JsonPath({"$.primaryText.simpleText", "$.primaryText.runs[0].text"})
    private String mTitle;

    @JsonPath({"$.secondaryText.simpleText", "$.secondaryText.runs[0].text"})
    private String mUserName;

    @JsonPath({"$.navigationEndpoint.watchEndpoint.videoId"})
    private String mVideoId;

    @JsonPath({"$.tertiaryText.runs[0].text"})
    private String mViewCountText;

    @JsonPath({"$.tertiaryText.simpleText"})
    private String mViewsAndPublished;

    public String getAccessibilityLength() {
        return this.mAccessibilityLength;
    }

    public String getChannelId() {
        return this.mChannelId;
    }

    public String getLengthText() {
        return this.mLengthText;
    }

    public int getPercentWatched() {
        return this.mPercentWatched;
    }

    public String getPlaylistId() {
        return this.mPlaylistId;
    }

    public int getPlaylistIndex() {
        return this.mPlaylistIndex;
    }

    public String getPublishedText() {
        String str = this.mPublishedText;
        if (str != null) {
            return str;
        }
        String str2 = this.mViewsAndPublished;
        if (str2 == null || !str2.contains(YouTubeHelper.TEXT_DELIM)) {
            return null;
        }
        return this.mViewsAndPublished.split(YouTubeHelper.TEXT_DELIM)[1];
    }

    public String getRichThumbnailUrl() {
        return this.mRichThumbnailUrl;
    }

    public List<Thumbnail> getThumbnails() {
        return this.mThumbnails;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getUserName() {
        return this.mUserName;
    }

    public String getVideoId() {
        return this.mVideoId;
    }

    public String getViewCountText() {
        String str = this.mViewCountText;
        if (str != null) {
            return str;
        }
        String str2 = this.mViewsAndPublished;
        if (str2 == null || !str2.contains(YouTubeHelper.TEXT_DELIM)) {
            return null;
        }
        return this.mViewsAndPublished.split(YouTubeHelper.TEXT_DELIM)[0];
    }

    public String getViewsAndPublished() {
        return this.mViewsAndPublished;
    }
}
