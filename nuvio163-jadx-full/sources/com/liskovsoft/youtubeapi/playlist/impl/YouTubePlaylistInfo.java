package com.liskovsoft.youtubeapi.playlist.impl;

import com.liskovsoft.mediaserviceinterfaces.data.ItemGroup;
import com.liskovsoft.mediaserviceinterfaces.data.PlaylistInfo;
import com.liskovsoft.youtubeapi.playlist.models.PlaylistInfoItem;
import com.liskovsoft.youtubeapi.playlist.models.PlaylistsResult;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class YouTubePlaylistInfo implements PlaylistInfo {
    private boolean mIsSelected;
    private String mPlaylistId;
    private String mTitle;

    public static List<PlaylistInfo> from(PlaylistsResult playlistsResult) {
        if (playlistsResult == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        Iterator<PlaylistInfoItem> it = playlistsResult.getPlaylists().iterator();
        while (it.hasNext()) {
            arrayList.add(from(it.next()));
        }
        return arrayList;
    }

    @Override // com.liskovsoft.mediaserviceinterfaces.data.PlaylistInfo
    public int getCurrentIndex() {
        return -1;
    }

    @Override // com.liskovsoft.mediaserviceinterfaces.data.PlaylistInfo
    public String getPlaylistId() {
        return this.mPlaylistId;
    }

    @Override // com.liskovsoft.mediaserviceinterfaces.data.PlaylistInfo
    public int getSize() {
        return -1;
    }

    @Override // com.liskovsoft.mediaserviceinterfaces.data.PlaylistInfo
    public String getTitle() {
        return this.mTitle;
    }

    @Override // com.liskovsoft.mediaserviceinterfaces.data.PlaylistInfo
    public boolean isSelected() {
        return this.mIsSelected;
    }

    public void setSelected(boolean z2) {
        this.mIsSelected = z2;
    }

    private static YouTubePlaylistInfo from(PlaylistInfoItem playlistInfoItem) {
        YouTubePlaylistInfo youTubePlaylistInfo = new YouTubePlaylistInfo();
        youTubePlaylistInfo.mTitle = playlistInfoItem.getTitle();
        youTubePlaylistInfo.mPlaylistId = playlistInfoItem.getPlaylistId();
        youTubePlaylistInfo.mIsSelected = playlistInfoItem.isSelected();
        return youTubePlaylistInfo;
    }

    public static YouTubePlaylistInfo from(ItemGroup itemGroup, boolean z2) {
        YouTubePlaylistInfo youTubePlaylistInfo = new YouTubePlaylistInfo();
        youTubePlaylistInfo.mTitle = itemGroup.getTitle();
        youTubePlaylistInfo.mPlaylistId = String.valueOf(itemGroup.getId());
        youTubePlaylistInfo.mIsSelected = z2;
        return youTubePlaylistInfo;
    }
}
