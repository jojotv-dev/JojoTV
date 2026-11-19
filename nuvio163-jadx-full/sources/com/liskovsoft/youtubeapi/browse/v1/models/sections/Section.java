package com.liskovsoft.youtubeapi.browse.v1.models.sections;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import com.liskovsoft.googlecommon.common.models.V2.TextItem;
import com.liskovsoft.sharedutils.helpers.Helpers;
import com.liskovsoft.youtubeapi.common.models.items.ItemWrapper;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class Section {

    @JsonPath({"$.headerRenderer.chipCloudRenderer.chips[*].chipCloudChipRenderer"})
    private List<Chip> mChips;

    @JsonPath({"$.content.horizontalListRenderer.items[*]"})
    private List<ItemWrapper> mItemWrappers;

    @JsonPath({"$.content.horizontalListRenderer.continuations[0].nextContinuationData.continuation"})
    private String mNextPageKey;

    @JsonPath({"$.title", "$.headerRenderer.shelfHeaderRenderer.title", "$.headerRenderer.shelfHeaderRenderer.avatarLockup.avatarLockupRenderer.title"})
    private TextItem mTitle;

    public List<Chip> getChips() {
        return this.mChips;
    }

    public List<ItemWrapper> getItemWrappers() {
        return this.mItemWrappers;
    }

    public String getNextPageKey() {
        return this.mNextPageKey;
    }

    public String getTitle() {
        TextItem textItem = this.mTitle;
        if (textItem != null) {
            return Helpers.toString(textItem.getText());
        }
        return null;
    }
}
