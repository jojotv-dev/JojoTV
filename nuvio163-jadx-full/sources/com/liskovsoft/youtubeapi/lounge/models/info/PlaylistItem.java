package com.liskovsoft.youtubeapi.lounge.models.info;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import com.liskovsoft.googlecommon.common.helpers.ServiceHelper;

/* JADX INFO: loaded from: classes4.dex */
public class PlaylistItem {

    @JsonPath({"$.duration"})
    private String mDurationText;

    @JsonPath({"$.encrypted_id"})
    private String mEncryptedId;

    @JsonPath({"$.length_seconds"})
    private String mLengthSeconds;

    @JsonPath({"$.thumbnail"})
    private String mThumbnailUrl;

    @JsonPath({"$.title"})
    private String mTitle;

    public String getDurationText() {
        return this.mDurationText;
    }

    public String getEncryptedId() {
        return this.mEncryptedId;
    }

    public String getLengthSeconds() {
        return this.mLengthSeconds;
    }

    public String getThumbnailUrl() {
        return ServiceHelper.tidyUrl(this.mThumbnailUrl);
    }

    public String getTitle() {
        return this.mTitle;
    }
}
