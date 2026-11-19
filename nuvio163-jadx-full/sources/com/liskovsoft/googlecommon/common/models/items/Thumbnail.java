package com.liskovsoft.googlecommon.common.models.items;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import com.liskovsoft.googlecommon.common.helpers.YouTubeHelper;

/* JADX INFO: loaded from: classes4.dex */
public class Thumbnail {

    @JsonPath({"$.height"})
    private int height;

    @JsonPath({"$.url"})
    private String url;

    @JsonPath({"$.width"})
    private int width;

    public int getHeight() {
        return this.height;
    }

    public String getUrl() {
        String str = this.url;
        if (str != null && str.startsWith("//")) {
            this.url = "https:" + this.url;
        }
        String strAvatarBlockFix = YouTubeHelper.avatarBlockFix(this.url);
        this.url = strAvatarBlockFix;
        return strAvatarBlockFix;
    }

    public int getWidth() {
        return this.width;
    }
}
