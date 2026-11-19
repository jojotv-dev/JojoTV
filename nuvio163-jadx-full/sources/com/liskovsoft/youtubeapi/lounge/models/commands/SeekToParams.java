package com.liskovsoft.youtubeapi.lounge.models.commands;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;

/* JADX INFO: loaded from: classes4.dex */
public class SeekToParams {

    @JsonPath({"$.newTime"})
    private String mNewTimeSec;

    public String getNewTimeSec() {
        return this.mNewTimeSec;
    }
}
