package com.liskovsoft.youtubeapi.browse.v1.models.sections;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import com.liskovsoft.youtubeapi.common.models.items.ItemWrapper;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class SectionContinuation {

    @JsonPath({"$.continuationContents.horizontalListContinuation.items[*]", "$.continuationContents.playlistVideoListContinuation.contents[*]"})
    private List<ItemWrapper> mItemWrappers;

    @JsonPath({"$.continuationContents.horizontalListContinuation.continuations[0].nextContinuationData.continuation", "$.continuationContents.playlistVideoListContinuation.continuations[0].nextContinuationData.continuation"})
    private String mNextPageKey;

    @JsonPath({"$.responseContext.visitorData"})
    private String mVisitorData;

    public List<ItemWrapper> getItemWrappers() {
        return this.mItemWrappers;
    }

    public String getNextPageKey() {
        return this.mNextPageKey;
    }

    public String getVisitorData() {
        return this.mVisitorData;
    }
}
