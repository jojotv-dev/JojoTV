package com.liskovsoft.youtubeapi.feedback.models;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import com.liskovsoft.googlecommon.common.models.V2.TextItem;

/* JADX INFO: loaded from: classes4.dex */
public class FeedbackReason {

    @JsonPath({"$.dismissalReasonTextRenderer.text"})
    private TextItem mTitle;

    @JsonPath({"$.dismissalReasonTextRenderer.feedback.feedbackEndpoint.feedbackToken"})
    private String mToken;

    public String getTitle() {
        TextItem textItem = this.mTitle;
        if (textItem != null) {
            return textItem.toString();
        }
        return null;
    }

    public String getToken() {
        return this.mToken;
    }
}
