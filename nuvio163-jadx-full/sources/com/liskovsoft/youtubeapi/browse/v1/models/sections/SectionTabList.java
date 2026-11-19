package com.liskovsoft.youtubeapi.browse.v1.models.sections;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class SectionTabList {

    @JsonPath({"$.contents.tvBrowseRenderer"})
    private SectionTab mSingleTab;

    @JsonPath({"$.contents.tvBrowseRenderer.content.tvSecondaryNavRenderer.sections[0].tvSecondaryNavSectionRenderer.tabs[*].tabRenderer"})
    private List<SectionTab> mTabs;

    @JsonPath({"$.responseContext.visitorData"})
    private String mVisitorData;

    public List<SectionTab> getTabs() {
        if (this.mTabs == null && this.mSingleTab != null) {
            ArrayList arrayList = new ArrayList();
            this.mTabs = arrayList;
            arrayList.add(this.mSingleTab);
        }
        return this.mTabs;
    }

    public String getVisitorData() {
        return this.mVisitorData;
    }
}
