package com.liskovsoft.youtubeapi.lounge.models.info;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class PlaylistInfo {

    @JsonPath({"$.author"})
    private String mAuthor;

    @JsonPath({"$.video[*]"})
    private List<PlaylistItem> mItems;

    @JsonPath({"$.title"})
    private String mTitle;

    public String getAuthor() {
        return this.mAuthor;
    }

    public List<PlaylistItem> getItems() {
        return this.mItems;
    }

    public String getTitle() {
        return this.mTitle;
    }
}
