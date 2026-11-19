package com.liskovsoft.youtubeapi.browse.v1.models.sections;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class SectionTabContinuation {

    @JsonPath({"$.continuationContents.sectionListContinuation.continuations[0].nextContinuationData.continuation"})
    private String mNextPageKey;

    @JsonPath({"$.continuationContents.sectionListContinuation.contents[*].shelfRenderer"})
    private List<Section> mSections;

    public String getNextPageKey() {
        return this.mNextPageKey;
    }

    public List<Section> getSections() {
        return this.mSections;
    }
}
