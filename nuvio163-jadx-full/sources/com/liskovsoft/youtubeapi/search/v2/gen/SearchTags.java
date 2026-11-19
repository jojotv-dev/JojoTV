package com.liskovsoft.youtubeapi.search.v2.gen;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class SearchTags {

    @JsonPath({"$[0]"})
    private String mSearchQuery;

    @JsonPath({"$[1][*][0]"})
    private List<String> mSearchTags;

    public String getSearchQuery() {
        return this.mSearchQuery;
    }

    public List<String> getSearchTags() {
        return this.mSearchTags;
    }
}
