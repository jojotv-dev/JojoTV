package com.liskovsoft.youtubeapi.lounge.models.commands;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;

/* JADX INFO: loaded from: classes4.dex */
public class PlaylistParams {

    @JsonPath({"$.ctt"})
    private String mCtt;

    @JsonPath({"$.currentTime"})
    private String mCurrentTimeSec;

    @JsonPath({"$.languageCode"})
    private String mLanguageCode;

    @JsonPath({"$.listId"})
    private String mPlaylistId;

    @JsonPath({"$.currentIndex"})
    private String mPlaylistIndex;

    @JsonPath({"$.videoId"})
    private String mVideoId;

    @JsonPath({"$.videoIds"})
    private String mVideoIds;

    public String getCtt() {
        return this.mCtt;
    }

    public String getCurrentTimeSec() {
        return this.mCurrentTimeSec;
    }

    public String getLanguageCode() {
        return this.mLanguageCode;
    }

    public String getPlaylistId() {
        return this.mPlaylistId;
    }

    public String getPlaylistIndex() {
        return this.mPlaylistIndex;
    }

    public String getVideoId() {
        return this.mVideoId;
    }

    public String[] getVideoIds() {
        String str = this.mVideoIds;
        if (str == null) {
            return null;
        }
        return str.split(",");
    }
}
