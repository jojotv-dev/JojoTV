package com.liskovsoft.youtubeapi.actions;

import com.liskovsoft.youtubeapi.common.helpers.PostDataHelper;

/* JADX INFO: loaded from: classes4.dex */
public class ActionsApiHelper {
    private static final String CHANNEL_ID_TEMPLATE = "\"channelIds\":[\"%s\"],\"params\":\"%s\"";
    private static final String VIDEO_ID_TEMPLATE = "\"target\":{\"videoId\":\"%s\"}";

    public static String getEmptyQuery() {
        return PostDataHelper.createQueryTV("\"nop\":\"false\"");
    }

    public static String getLikeActionQuery(String str) {
        return PostDataHelper.createQueryTV("\"target\":{\"videoId\":\"" + str + "\"}");
    }

    public static String getSubscribeActionQuery(String str, String str2) {
        if (str2 == null) {
            str2 = "";
        }
        return PostDataHelper.createQueryTV("\"channelIds\":[\"" + str + "\"],\"params\":\"" + str2 + "\"");
    }
}
