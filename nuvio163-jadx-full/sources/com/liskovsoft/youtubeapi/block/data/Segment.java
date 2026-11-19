package com.liskovsoft.youtubeapi.block.data;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;

/* JADX INFO: loaded from: classes4.dex */
public class Segment {

    @JsonPath({"$.actionType"})
    private String mActionType;

    @JsonPath({"$.category"})
    private String mCategory;

    @JsonPath({"$.segment[1]"})
    private float mEnd;

    @JsonPath({"$.segment[0]"})
    private float mStart;

    @JsonPath({"$.UUID"})
    private String mUuid;

    public String getActionType() {
        return this.mActionType;
    }

    public String getCategory() {
        return this.mCategory;
    }

    public float getEnd() {
        return this.mEnd;
    }

    public float getStart() {
        return this.mStart;
    }

    public String getUuid() {
        return this.mUuid;
    }
}
