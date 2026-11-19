package com.liskovsoft.youtubeapi.next.v1.models;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;

/* JADX INFO: loaded from: classes4.dex */
public class Playlist {

    @JsonPath({"$.playlistId"})
    private String mPlaylistId;

    @JsonPath({"$.currentIndex"})
    private int mPlaylistIndex = -1;

    @JsonPath({"$.title"})
    private String mTitle;

    @JsonPath({"$.totalVideos"})
    private int mTotalVideos;

    public String getPlaylistId() {
        return this.mPlaylistId;
    }

    public int getPlaylistIndex() {
        return this.mPlaylistIndex;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public int getTotalVideos() {
        return this.mTotalVideos;
    }
}
