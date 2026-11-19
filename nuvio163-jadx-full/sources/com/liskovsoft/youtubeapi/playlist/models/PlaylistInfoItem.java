package com.liskovsoft.youtubeapi.playlist.models;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;

/* JADX INFO: loaded from: classes4.dex */
public class PlaylistInfoItem {
    private static final String CONTAINS_SELECTED_ALL = "ALL";
    private static final String CONTAINS_SELECTED_NONE = "NONE";

    @JsonPath({"$.containsSelectedVideos"})
    private String mContainsSelected;

    @JsonPath({"$.playlistId"})
    private String mPlaylistId;

    @JsonPath({"$.title.simpleText"})
    private String mTitle;

    public String getContainsSelected() {
        return this.mContainsSelected;
    }

    public String getPlaylistId() {
        return this.mPlaylistId;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public boolean isSelected() {
        return CONTAINS_SELECTED_ALL.equals(this.mContainsSelected);
    }
}
