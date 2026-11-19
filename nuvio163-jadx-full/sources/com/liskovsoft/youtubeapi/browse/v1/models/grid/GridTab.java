package com.liskovsoft.youtubeapi.browse.v1.models.grid;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import com.liskovsoft.googlecommon.common.models.items.Thumbnail;
import com.liskovsoft.sharedutils.helpers.Helpers;
import com.liskovsoft.youtubeapi.common.models.items.ItemWrapper;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class GridTab {
    private static final String PRESENTATION_NEW_CONTENT = "NEW_CONTENT";

    @JsonPath({"$.endpoint.browseEndpoint.browseId"})
    private String mBrowseId;

    @JsonPath({"$.content.tvSurfaceContentRenderer.content.gridRenderer.items[*]", "$.contents[0].itemSectionRenderer.contents[*]"})
    private List<ItemWrapper> mItemWrappers;

    @JsonPath({"$.content.tvSurfaceContentRenderer.content.gridRenderer.continuations[0].nextContinuationData.continuation", "$.continuations[0].nextContinuationData.continuation"})
    private String mNextPageKey;

    @JsonPath({"$.endpoint.browseEndpoint.params"})
    private String mParams;

    @JsonPath({"$.presentationStyle.style"})
    private String mPresentationStyle;

    @JsonPath({"$.content.tvSurfaceContentRenderer.continuation.reloadContinuationData.continuation"})
    private String mReloadPageKey;

    @JsonPath({"$.thumbnail.thumbnails[*]"})
    private List<Thumbnail> mThumbnails;

    @JsonPath({"$.title"})
    private String mTitle;

    @JsonPath({"$.accessibility.accessibilityData.label"})
    private String mTitleAlt;

    @JsonPath({"$.unselectable"})
    private boolean mUnselectable;

    public boolean equals(Object obj) {
        if (obj instanceof GridTab) {
            GridTab gridTab = (GridTab) obj;
            if (Helpers.equals(this.mTitle, gridTab.getTitle()) && Helpers.equals(this.mReloadPageKey, gridTab.getReloadPageKey()) && Helpers.equals(this.mNextPageKey, gridTab.getNextPageKey())) {
                return true;
            }
        }
        return false;
    }

    public String getBrowseId() {
        return this.mBrowseId;
    }

    public List<ItemWrapper> getItemWrappers() {
        return this.mItemWrappers;
    }

    public String getNextPageKey() {
        return this.mNextPageKey;
    }

    public String getParams() {
        return this.mParams;
    }

    public String getReloadPageKey() {
        return this.mReloadPageKey;
    }

    public List<Thumbnail> getThumbnails() {
        return this.mThumbnails;
    }

    public String getTitle() {
        return this.mTitle;
    }

    public String getTitleAlt() {
        return this.mTitleAlt;
    }

    public boolean hasNewContent() {
        return PRESENTATION_NEW_CONTENT.equals(this.mPresentationStyle);
    }

    public boolean isUnselectable() {
        return this.mUnselectable;
    }
}
