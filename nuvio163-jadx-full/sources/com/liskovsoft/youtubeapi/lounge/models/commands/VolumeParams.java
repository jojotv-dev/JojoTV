package com.liskovsoft.youtubeapi.lounge.models.commands;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;

/* JADX INFO: loaded from: classes4.dex */
public class VolumeParams {

    @JsonPath({"$.delta"})
    private String mDelta;

    @JsonPath({"$.volume"})
    private String mVolume;

    public String getDelta() {
        return this.mDelta;
    }

    public String getVolume() {
        return this.mVolume;
    }
}
