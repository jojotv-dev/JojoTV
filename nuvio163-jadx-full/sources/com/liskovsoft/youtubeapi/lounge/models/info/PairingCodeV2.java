package com.liskovsoft.youtubeapi.lounge.models.info;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import com.liskovsoft.googlecommon.common.helpers.ServiceHelper;

/* JADX INFO: loaded from: classes4.dex */
public class PairingCodeV2 {

    @JsonPath({"$.code"})
    private String mPairingCode;
    private String mPairingCodeAlt;

    public String getPairingCode() {
        String str = this.mPairingCode;
        if (str == null) {
            return null;
        }
        if (this.mPairingCodeAlt == null) {
            this.mPairingCodeAlt = ServiceHelper.insertSeparator(str, " ", 3);
        }
        return this.mPairingCodeAlt;
    }
}
