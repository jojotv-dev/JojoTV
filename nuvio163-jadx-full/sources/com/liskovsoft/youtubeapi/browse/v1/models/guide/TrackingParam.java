package com.liskovsoft.youtubeapi.browse.v1.models.guide;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class TrackingParam {
    public static final String SERVICE_GFEEDBACK = "GFEEDBACK";
    public static final String SERVICE_SUGGEST = "SUGGEST";

    @JsonPath({"$.params"})
    private List<Param> mParams;

    @JsonPath({"$.service"})
    private String mService;

    public static class Param {
        public static final String KEY_E = "e";
        public static final String KEY_LOGGED_IN = "logged_in";
        public static final String KEY_SUGGESTXP = "sugexp";
        public static final String KEY_SUGGEST_TOKEN = "suggest_token";

        @JsonPath({"$.key"})
        private String mKey;

        @JsonPath({"$.value"})
        private String mValue;

        public String getKey() {
            return this.mKey;
        }

        public String getValue() {
            return this.mValue;
        }
    }

    public List<Param> getParams() {
        return this.mParams;
    }

    public String getService() {
        return this.mService;
    }
}
