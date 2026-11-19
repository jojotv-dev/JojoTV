package com.liskovsoft.youtubeapi.lounge.models.info;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class TokenInfoList {

    @JsonPath({"$.screens[*]"})
    private List<TokenInfo> mTokenInfos;

    public List<TokenInfo> getTokenInfos() {
        return this.mTokenInfos;
    }
}
