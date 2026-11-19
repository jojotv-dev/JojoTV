package com.liskovsoft.googlecommon.common.models.V2;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import com.liskovsoft.googlecommon.common.helpers.ServiceHelper;
import com.liskovsoft.sharedutils.helpers.Helpers;

/* JADX INFO: loaded from: classes4.dex */
public class TextItem {

    @JsonPath({"$.simpleText"})
    private String mFullText;

    @JsonPath({"$.runs[0].text"})
    private String mText1;

    @JsonPath({"$.runs[1].text"})
    private String mText2;

    @JsonPath({"$.runs[2].text"})
    private String mText3;

    public CharSequence getText() {
        return ServiceHelper.combineText(this.mText1, this.mText2, this.mText3, this.mFullText);
    }

    public String toString() {
        return Helpers.toString(getText());
    }
}
