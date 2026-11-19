package com.liskovsoft.googlecommon.common.models.auth.info;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import com.liskovsoft.googlecommon.common.models.V2.TextItem;
import com.liskovsoft.googlecommon.common.models.items.Thumbnail;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class AccountInt {

    @JsonPath({"$.accountPhoto.thumbnails[*]"})
    private List<Thumbnail> mAccountThumbnails;

    @JsonPath({"$.channelHandle"})
    private TextItem mChannelName;

    @JsonPath({"$.accountByline"})
    private TextItem mEmail;

    @JsonPath({"$.hasChannel"})
    private boolean mHasChannel;

    @JsonPath({"$.isDisabled"})
    private boolean mIsDisabled;

    @JsonPath({"$.isSelected"})
    private boolean mIsSelected;

    @JsonPath({"$.accountName"})
    private TextItem mName;

    @JsonPath({"$.serviceEndpoint.selectActiveIdentityEndpoint.supportedTokens[*].pageIdToken.pageId"})
    private List<String> mPageIdTokens;

    public String getChannelName() {
        TextItem textItem = this.mChannelName;
        if (textItem != null) {
            return textItem.toString();
        }
        return null;
    }

    public String getEmail() {
        TextItem textItem = this.mEmail;
        if (textItem != null) {
            return textItem.toString();
        }
        return null;
    }

    public String getName() {
        TextItem textItem = this.mName;
        if (textItem != null) {
            return textItem.toString();
        }
        return null;
    }

    public String getPageIdToken() {
        List<String> list = this.mPageIdTokens;
        if (list != null) {
            return list.get(0);
        }
        return null;
    }

    public List<Thumbnail> getThumbnails() {
        return this.mAccountThumbnails;
    }

    public boolean hasChannel() {
        return this.mHasChannel;
    }

    public boolean isDisabled() {
        return this.mIsDisabled;
    }

    public boolean isSelected() {
        return this.mIsSelected;
    }
}
