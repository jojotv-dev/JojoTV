package com.liskovsoft.youtubeapi.common.models.items;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import com.liskovsoft.youtubeapi.common.models.V2.TileItem;

/* JADX INFO: loaded from: classes4.dex */
public class ItemWrapper {

    @JsonPath({"$.gridChannelRenderer", "$.pivotChannelRenderer", "$.compactChannelRenderer"})
    private ChannelItem mChannelItem;

    @JsonPath({"$.tvMusicVideoRenderer"})
    private MusicItem mMusicItem;

    @JsonPath({"$.gridPlaylistRenderer", "$.pivotPlaylistRenderer", "$.compactPlaylistRenderer"})
    private PlaylistItem mPlaylistItem;

    @JsonPath({"$.gridRadioRenderer", "$.pivotRadioRenderer", "$.compactRadioRenderer"})
    private RadioItem mRadioItem;

    @JsonPath({"$.tileRenderer"})
    private TileItem mTileItem;

    @JsonPath({"$.gridVideoRenderer", "$.pivotVideoRenderer", "$.compactVideoRenderer"})
    private VideoItem mVideoItem;

    public ChannelItem getChannelItem() {
        return this.mChannelItem;
    }

    public MusicItem getMusicItem() {
        return this.mMusicItem;
    }

    public PlaylistItem getPlaylistItem() {
        return this.mPlaylistItem;
    }

    public RadioItem getRadioItem() {
        return this.mRadioItem;
    }

    public TileItem getTileItem() {
        return this.mTileItem;
    }

    public VideoItem getVideoItem() {
        return this.mVideoItem;
    }

    public boolean isLive() {
        VideoItem videoItem = this.mVideoItem;
        if (videoItem != null) {
            return videoItem.isLive();
        }
        TileItem tileItem = this.mTileItem;
        if (tileItem != null) {
            return tileItem.isLive();
        }
        return false;
    }

    public boolean isMovie() {
        VideoItem videoItem = this.mVideoItem;
        if (videoItem != null) {
            return videoItem.isMovie();
        }
        TileItem tileItem = this.mTileItem;
        if (tileItem != null) {
            return tileItem.isMovie();
        }
        return false;
    }

    public boolean isUpcoming() {
        VideoItem videoItem = this.mVideoItem;
        if (videoItem != null) {
            return videoItem.isUpcoming();
        }
        TileItem tileItem = this.mTileItem;
        if (tileItem != null) {
            return tileItem.isUpcoming();
        }
        return false;
    }
}
