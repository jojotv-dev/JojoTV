package com.liskovsoft.youtubeapi.browse.v1.models.sections;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import com.liskovsoft.youtubeapi.common.models.items.ItemWrapper;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class Chip {

    @JsonPath({"$.content.horizontalListRenderer.items[*]"})
    private List<ItemWrapper> mItemWrappers;

    @JsonPath({"$.content.horizontalListRenderer.continuations[0].nextContinuationData.continuation"})
    private String mNextPageKey;

    @JsonPath({"$.content.horizontalListRenderer.continuations[0].reloadContinuationData.continuation"})
    private String mReloadPageKey;

    @JsonPath({"$.text.simpleText"})
    private String mTitle;

    public List<ItemWrapper> getItemWrappers() {
        return this.mItemWrappers;
    }

    public String getNextPageKey() {
        return this.mNextPageKey;
    }

    public String getReloadPageKey() {
        return this.mReloadPageKey;
    }

    public String getTitle() {
        return this.mTitle;
    }
}
