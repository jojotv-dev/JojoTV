package com.liskovsoft.youtubeapi.search.v1.models;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import com.liskovsoft.youtubeapi.common.models.items.ItemWrapper;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class SearchResultContinuation {

    @JsonPath({"$.estimatedResults"})
    private String mEstimatedResults;

    @JsonPath({"$.continuationContents.sectionListContinuation.contents[0].shelfRenderer.content.horizontalListRenderer.items[*]", "$.continuationContents.horizontalListContinuation.items[*]", "$.continuationContents.itemSectionContinuation.contents[*]", "$.continuationContents.sectionListContinuation.contents[0].itemSectionRenderer.contents[*]"})
    private List<ItemWrapper> mItemWrappers;

    @JsonPath({"$.continuationContents.sectionListContinuation.contents[0].shelfRenderer.content.horizontalListRenderer.continuations[0].nextContinuationData.continuation", "$.continuationContents.horizontalListContinuation.continuations[0].nextContinuationData.continuation", "$.continuationContents.sectionListContinuation.contents[0].itemSectionRenderer.continuations[0].nextContinuationData.continuation", "$.continuationContents.itemSectionContinuation.continuations[0].nextContinuationData.continuation"})
    private String mNextPageKey;

    public String getEstimatedResults() {
        return this.mEstimatedResults;
    }

    public List<ItemWrapper> getItemWrappers() {
        return this.mItemWrappers;
    }

    public String getNextPageKey() {
        return this.mNextPageKey;
    }
}
