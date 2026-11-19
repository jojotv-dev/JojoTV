package com.liskovsoft.youtubeapi.common.models.items;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import com.liskovsoft.googlecommon.common.models.items.Thumbnail;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class RadioItem {

    @JsonPath({"$.longBylineText.simpleText"})
    private String mDescription;

    @JsonPath({"$.navigationEndpoint.watchEndpoint.params"})
    private String mParams;

    @JsonPath({"$.playlistId", "$.navigationEndpoint.watchEndpoint.playlistId"})
    private String mPlaylistId;

    @JsonPath({"$.secondaryNavigationEndpoint.watchEndpoint.videoId"})
    private String mSecondVideoId;

    @JsonPath({"$.thumbnailText.runs[0].text"})
    private String mThumbnailText1;

    @JsonPath({"$.thumbnailText.runs[1].text"})
    private String mThumbnailText2;

    @JsonPath({"$.thumbnailRenderer.playlistVideoThumbnailRenderer.thumbnail.thumbnails[*]", "$.thumbnail.thumbnails[*]"})
    private List<Thumbnail> mThumbnails;

    @JsonPath({"$.title.runs[0].text", "$.title.simpleText"})
    private String mTitle;

    @JsonPath({"$.videoCountShortText.runs[0].text"})
    private String mVideoCountShortText;

    @JsonPath({"$.videoCountText.runs[0].text"})
    private String mVideoCountText;

    @JsonPath({"$.navigationEndpoint.watchEndpoint.videoId"})
    private String mVideoId;

    public String getDescription() {
        return this.mDescription;
    }

    public String getParams() {
        return this.mParams;
    }

    public String getPlaylistId() {
        return this.mPlaylistId;
    }

    public String getSecondVideoId() {
        return this.mSecondVideoId;
    }

    public String getThumbnailText() {
        return this.mThumbnailText1 + " " + this.mThumbnailText2;
    }

    public List<Thumbnail> getThumbnails() {
        return this.mThumbnails;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getVideoCountShortText() {
        return this.mVideoCountShortText;
    }

    public String getVideoCountText() {
        return this.mVideoCountText;
    }

    public String getVideoId() {
        return this.mVideoId;
    }
}
