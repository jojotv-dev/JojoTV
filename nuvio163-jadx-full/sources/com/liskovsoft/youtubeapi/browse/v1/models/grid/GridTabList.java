package com.liskovsoft.youtubeapi.browse.v1.models.grid;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import java.util.Collections;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class GridTabList {

    @JsonPath({"$.contents.tvBrowseRenderer.content.tvSurfaceContentRenderer.content.sectionListRenderer"})
    private GridTab mHistoryTab;

    @JsonPath({"$.contents.tvBrowseRenderer.content.tvSecondaryNavRenderer.sections[0].tvSecondaryNavSectionRenderer.tabs[*].tabRenderer"})
    private List<GridTab> mTabs;

    @JsonPath({"$.responseContext.visitorData"})
    private String mVisitorData;

    public List<GridTab> getTabs() {
        List<GridTab> list = this.mTabs;
        return list != null ? list : Collections.singletonList(this.mHistoryTab);
    }

    public String getVisitorData() {
        return this.mVisitorData;
    }
}
