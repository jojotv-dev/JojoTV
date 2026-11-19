package com.liskovsoft.youtubeapi.browse.v1.models.sections;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class SectionTab {

    @JsonPath({"$.endpoint.browseEndpoint.browseId"})
    private String browseId;

    @JsonPath({"$.content.tvSurfaceContentRenderer.content.sectionListRenderer.continuations[0].nextContinuationData.continuation"})
    private String mNextPageKey;

    @JsonPath({"$.content.tvSurfaceContentRenderer.continuation.reloadContinuationData.continuation"})
    private String mReloadPageKey;

    @JsonPath({"$.content.tvSurfaceContentRenderer.content.sectionListRenderer.contents[*].shelfRenderer"})
    private List<Section> mSections;

    @JsonPath({"$.title"})
    private String title;

    public String getBrowseId() {
        return this.browseId;
    }

    public String getNextPageKey() {
        return this.mNextPageKey;
    }

    public String getReloadPageKey() {
        return this.mReloadPageKey;
    }

    public List<Section> getSections() {
        return this.mSections;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean isEmpty() {
        return this.mSections == null;
    }
}
