package com.liskovsoft.youtubeapi.lounge.models.bind;

import com.liskovsoft.googlecommon.common.converters.regexp.RegExp;
import com.liskovsoft.googlecommon.common.helpers.ServiceHelper;
import io.sentry.q6;

/* JADX INFO: loaded from: classes4.dex */
public class PairingCode {

    @RegExp({q6.DEFAULT_PROPAGATION_TARGETS})
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
