package com.liskovsoft.youtubeapi.next.v1.models;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import com.liskovsoft.googlecommon.common.models.items.Thumbnail;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class NextVideo {

    @JsonPath({"$.item.previewButtonRenderer.byline.simpleText", "$.item.previewButtonRenderer.byline.runs[0].text"})
    private String mAuthor;

    @JsonPath({"$.endpoint.watchEndpoint.params"})
    private String mParams;

    @JsonPath({"$.endpoint.watchEndpoint.playlistId"})
    private String mPlaylistId;

    @JsonPath({"$.endpoint.watchEndpoint.index"})
    private int mPlaylistItemIndex = -1;

    @JsonPath({"$.item.previewButtonRenderer.thumbnail.thumbnails[*]"})
    private List<Thumbnail> mThumbnails;

    @JsonPath({"$.item.previewButtonRenderer.title.simpleText", "$.item.previewButtonRenderer.title.runs[0].text"})
    private String mTitle;

    @JsonPath({"$.endpoint.watchEndpoint.videoId"})
    private String mVideoId;

    public String getAuthor() {
        return this.mAuthor;
    }

    public String getPlaylistId() {
        return this.mPlaylistId;
    }

    public int getPlaylistItemIndex() {
        return this.mPlaylistItemIndex;
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
}
