package com.liskovsoft.youtubeapi.feedback;

import com.liskovsoft.youtubeapi.common.helpers.PostDataHelper;

/* JADX INFO: loaded from: classes4.dex */
public class FeedbackApiHelper {
    private static final String NOT_INTERESTED_QUERY = "\"feedbackTokens\":[\"%s\"]";

    public static String getNotInterestedQuery(String str) {
        return PostDataHelper.createQueryTV("\"feedbackTokens\":[\"" + str + "\"]");
    }
}
