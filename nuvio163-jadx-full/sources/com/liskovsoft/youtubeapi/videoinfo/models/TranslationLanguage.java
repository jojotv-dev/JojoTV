package com.liskovsoft.youtubeapi.videoinfo.models;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import com.liskovsoft.googlecommon.common.models.V2.TextItem;

/* JADX INFO: loaded from: classes4.dex */
public class TranslationLanguage {

    @JsonPath({"$.languageCode"})
    private String mLanguageCode;

    @JsonPath({"$.languageName"})
    private TextItem mLanguageName;

    public String getLanguageCode() {
        return this.mLanguageCode;
    }

    public String getLanguageName() {
        TextItem textItem = this.mLanguageName;
        if (textItem != null) {
            return textItem.toString();
        }
        return null;
    }
}
