package com.fasterxml.jackson.databind.jsonFormatVisitors;

import androidx.media3.extractor.text.ttml.TtmlNode;
import com.fasterxml.jackson.annotation.JsonValue;

/* JADX INFO: loaded from: classes3.dex */
public enum JsonValueFormat {
    COLOR(TtmlNode.ATTR_TTS_COLOR),
    DATE("date"),
    DATE_TIME("date-time"),
    EMAIL("email"),
    HOST_NAME("host-name"),
    IP_ADDRESS("ip-address"),
    IPV6("ipv6"),
    PHONE("phone"),
    REGEX("regex"),
    STYLE("style"),
    TIME("time"),
    URI("uri"),
    UTC_MILLISEC("utc-millisec"),
    UUID("uuid");

    private final String _desc;

    JsonValueFormat(String str) {
        this._desc = str;
    }

    @Override // java.lang.Enum
    @JsonValue
    public String toString() {
        return this._desc;
    }
}
