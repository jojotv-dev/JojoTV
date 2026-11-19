package com.liskovsoft.youtubeapi.common.models.items;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import com.liskovsoft.googlecommon.common.models.items.Thumbnail;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class PlaylistItem {

    @JsonPath({"$.shortBylineText.runs[0].navigationEndpoint.browseEndpoint.browseId"})
    private String mChannelId;

    @JsonPath({"$.shortBylineText.runs[0].text"})
    private String mDescription;

    @JsonPath({"$.navigationEndpoint.watchEndpoint.params"})
    private String mParams;

    @JsonPath({"$.playlistId", "$.navigationEndpoint.watchEndpoint.playlistId"})
    private String mPlaylistId;

    @JsonPath({"$.thumbnail.thumbnails[*]", "$.thumbnailRenderer.playlistCustomThumbnailRenderer.thumbnail.thumbnails[*]", "$.thumbnailRenderer.playlistVideoThumbnailRenderer.thumbnail.thumbnails[*]"})
    private List<Thumbnail> mThumbnails;

    @JsonPath({"$.title.simpleText", "$.title.runs[0].text"})
    private String mTitle;

    @JsonPath({"$.videoCountText.runs[0].text"})
    private String mVideoCountText;

    @JsonPath({"$.navigationEndpoint.watchEndpoint.videoId"})
    private String mVideoId;

    public String getChannelId() {
        return this.mChannelId;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public String getParams() {
        return this.mParams;
    }

    public String getPlaylistId() {
        return this.mPlaylistId;
    }

    public List<Thumbnail> getThumbnails() {
        return this.mThumbnails;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getVideoCountText() {
        return this.mVideoCountText;
    }

    public String getVideoId() {
        return this.mVideoId;
    }
}
