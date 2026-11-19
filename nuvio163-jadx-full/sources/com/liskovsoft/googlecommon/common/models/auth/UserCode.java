package com.liskovsoft.googlecommon.common.models.auth;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;

/* JADX INFO: loaded from: classes4.dex */
public class UserCode {

    @JsonPath({"$.device_code"})
    private String mDeviceCode;

    @JsonPath({"$.expires_in"})
    private int mExpiresIn;

    @JsonPath({"$.interval"})
    private int mInterval;

    @JsonPath({"$.user_code"})
    private String mUserCode;
    private String mUserCodePretty;

    @JsonPath({"$.verification_url"})
    private String mVerificationUrl;

    public String getDeviceCode() {
        return this.mDeviceCode;
    }

    public int getExpiresIn() {
        return this.mExpiresIn;
    }

    public int getInterval() {
        return this.mInterval;
    }

    public String getUserCode() {
        String str = this.mUserCode;
        if (str == null) {
            return null;
        }
        if (this.mUserCodePretty == null) {
            this.mUserCodePretty = str.replace("-", " ");
        }
        return this.mUserCodePretty;
    }

    public String getVerificationUrl() {
        return this.mVerificationUrl;
    }
}
