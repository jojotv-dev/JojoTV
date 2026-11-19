package com.liskovsoft.youtubeapi.feedback.models;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import com.liskovsoft.googlecommon.common.models.V2.TextItem;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class FeedbackResponse {

    @JsonPath({"$.feedbackResponses[0].isProcessed"})
    private boolean mIsProcessed;

    @JsonPath({"$.feedbackResponses[0].followUpDialog.dismissalFollowUpRenderer.reasons"})
    private List<FeedbackReason> mReasons;

    @JsonPath({"$.feedbackResponses[0].followUpDialog.dismissalFollowUpRenderer.dismissalReasonsPrompt"})
    private TextItem mTitle;

    public List<FeedbackReason> getReasons() {
        return this.mReasons;
    }

    public String getTitle() {
        TextItem textItem = this.mTitle;
        if (textItem != null) {
            return textItem.toString();
        }
        return null;
    }

    public boolean isProcessed() {
        return this.mIsProcessed;
    }
}
