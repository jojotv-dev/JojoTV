package com.liskovsoft.googlecommon.common.models.auth;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;

/* JADX INFO: loaded from: classes4.dex */
public class AccessToken extends ErrorResponse {

    @JsonPath({"$.access_token"})
    private String mAccessToken;

    @JsonPath({"$.expires_in"})
    private int mExpiresIn;

    @JsonPath({"$.refresh_token"})
    private String mRefreshToken;

    @JsonPath({"$.scope"})
    private String mScope;

    @JsonPath({"$.token_type"})
    private String mTokenType;

    public String getAccessToken() {
        return this.mAccessToken;
    }

    public int getExpiresIn() {
        return this.mExpiresIn;
    }

    public String getRefreshToken() {
        return this.mRefreshToken;
    }

    public String getScope() {
        return this.mScope;
    }

    public String getTokenType() {
        return this.mTokenType;
    }
}
