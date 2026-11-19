package com.liskovsoft.youtubeapi.next.v1.result;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import com.liskovsoft.youtubeapi.common.models.items.ItemWrapper;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class WatchNextResultContinuation {

    @JsonPath({"$.continuationContents.horizontalListContinuation.items[*]"})
    private List<ItemWrapper> mItemWrappers;

    @JsonPath({"$.continuationContents.horizontalListContinuation.continuations[0].nextContinuationData.continuation", "$.continuationContents.horizontalListContinuation.continuations[1].nextContinuationData.continuation"})
    private String mNextPageKey;

    @JsonPath({"$.contents.singleColumnWatchNextResults.results.results.trackingParams"})
    private String mTrackingParams;

    public List<ItemWrapper> getItemWrappers() {
        return this.mItemWrappers;
    }

    public String getNextPageKey() {
        return this.mNextPageKey;
    }
}
