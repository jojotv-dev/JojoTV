package com.liskovsoft.googlecommon.common.models.auth;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;

/* JADX INFO: loaded from: classes4.dex */
public class ErrorResponse {

    @JsonPath({"$.error"})
    private String mError;

    @JsonPath({"$.error_description"})
    private String mErrorDescription;

    public String getError() {
        return this.mError;
    }

    public String getErrorDescription() {
        return this.mErrorDescription;
    }
}
