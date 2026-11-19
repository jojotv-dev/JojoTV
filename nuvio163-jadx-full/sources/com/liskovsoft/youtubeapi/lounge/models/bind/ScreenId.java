package com.liskovsoft.youtubeapi.lounge.models.bind;

import com.liskovsoft.googlecommon.common.converters.regexp.RegExp;
import io.sentry.q6;

/* JADX INFO: loaded from: classes4.dex */
public class ScreenId {

    @RegExp({q6.DEFAULT_PROPAGATION_TARGETS})
    private String mScreenId;

    public String getScreenId() {
        return this.mScreenId;
    }
}
