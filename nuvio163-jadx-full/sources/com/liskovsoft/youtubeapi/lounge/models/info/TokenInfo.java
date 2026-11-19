package com.liskovsoft.youtubeapi.lounge.models.info;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import com.liskovsoft.sharedutils.helpers.Helpers;
import w.h0;

/* JADX INFO: loaded from: classes4.dex */
public class TokenInfo {

    @JsonPath({"$.loungeToken"})
    private String mLoungeToken;

    @JsonPath({"$.screenId"})
    private String mScreenId;

    public static TokenInfo from(String str) {
        if (str == null) {
            return null;
        }
        String[] strArrSplit = str.split(",");
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.mScreenId = Helpers.parseStr(strArrSplit, 0);
        tokenInfo.mLoungeToken = Helpers.parseStr(strArrSplit, 1);
        return tokenInfo;
    }

    public String getLoungeToken() {
        return this.mLoungeToken;
    }

    public String getScreenId() {
        return this.mScreenId;
    }

    public String toString() {
        return h0.l(this.mScreenId, ",", this.mLoungeToken);
    }
}
