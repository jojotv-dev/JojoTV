package com.liskovsoft.youtubeapi.lounge.models.commands;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;

/* JADX INFO: loaded from: classes4.dex */
public class RemoteParams {
    private static final String PAIRING_TYPE_MANUAL = "manual";

    @JsonPath({"$.id"})
    private String mDeviceId;

    @JsonPath({"$.name"})
    private String mDeviceName;

    @JsonPath({"$.pairingType"})
    private String mPairingType;

    public String getDeviceId() {
        return this.mDeviceId;
    }

    public String getDeviceName() {
        return this.mDeviceName;
    }

    public String getPairingType() {
        return this.mPairingType;
    }

    public boolean isPairedManually() {
        return PAIRING_TYPE_MANUAL.equals(this.mPairingType);
    }
}
