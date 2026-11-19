package com.liskovsoft.youtubeapi.formatbuilders.utils;

import java.util.Arrays;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public final class ITagUtils {
    public static final String AUDIO_128K_AAC = "140";
    public static final String AUDIO_133K_WEBM = "171";
    public static final String AUDIO_156K_WEBM = "251";
    public static final String AUDIO_48K_AAC = "139";
    public static final String AUDIO_68K_WEBM = "249";
    public static final String AUDIO_89K_WEBM = "250";
    public static final String AVC = "AVC";
    public static final String MUXED_360P_AVC = "18";
    public static final String MUXED_360P_WEBM = "43";
    public static final String MUXED_720P_AVC = "22";
    public static final String VIDEO_1080P_AVC = "137";
    public static final String VIDEO_1080P_AVC_60FPS = "299";
    public static final String VIDEO_1080P_WEBM = "248";
    public static final String VIDEO_1080P_WEBM_60FPS_HDR = "335";
    public static final String VIDEO_1440P_AVC = "264";
    public static final String VIDEO_1440P_WEBM = "271";
    public static final String VIDEO_1440P_WEBM_60FPS = "308";
    public static final String VIDEO_1440P_WEBM_60FPS_HDR = "336";
    public static final String VIDEO_144P_AVC = "160";
    public static final String VIDEO_144P_WEBM = "278";
    public static final String VIDEO_2160P_AVC = "266";
    public static final String VIDEO_2160P_AVC_HQ = "138";
    public static final String VIDEO_2160P_WEBM = "313";
    public static final String VIDEO_2160P_WEBM_60FPS = "315";
    public static final String VIDEO_2160P_WEBM_60FPS_HDR = "337";
    public static final String VIDEO_240P_AVC = "133";
    public static final String VIDEO_240P_WEBM = "242";
    public static final String VIDEO_360P_AVC = "134";
    public static final String VIDEO_360P_WEBM = "243";
    public static final String VIDEO_480P_AVC = "135";
    public static final String VIDEO_480P_WEBM = "244";
    public static final String VIDEO_720P_AVC = "136";
    public static final String VIDEO_720P_AVC_60FPS = "298";
    public static final String VIDEO_720P_WEBM = "247";
    public static final String VIDEO_720P_WEBM_60FPS_HDR = "334";
    public static final String WEBM = "VP9";
    private static final List<List<String>> sITagsContainer;
    private static final List<String> sOrderedITagsAVC;
    private static final List<String> sOrderedITagsWEBM;

    static {
        List<String> listAsList = Arrays.asList(MUXED_360P_AVC, MUXED_720P_AVC, AUDIO_48K_AAC, AUDIO_128K_AAC, VIDEO_144P_AVC, VIDEO_240P_AVC, VIDEO_360P_AVC, VIDEO_480P_AVC, VIDEO_720P_AVC, VIDEO_720P_AVC_60FPS, VIDEO_1080P_AVC, VIDEO_1080P_AVC_60FPS, VIDEO_1440P_AVC, VIDEO_2160P_AVC, VIDEO_2160P_AVC_HQ);
        sOrderedITagsAVC = listAsList;
        List<String> listAsList2 = Arrays.asList(MUXED_360P_WEBM, AUDIO_68K_WEBM, AUDIO_89K_WEBM, AUDIO_133K_WEBM, AUDIO_156K_WEBM, VIDEO_144P_WEBM, VIDEO_240P_WEBM, VIDEO_360P_WEBM, VIDEO_480P_WEBM, VIDEO_720P_WEBM, VIDEO_720P_WEBM_60FPS_HDR, VIDEO_1080P_WEBM, VIDEO_1080P_WEBM_60FPS_HDR, VIDEO_1440P_WEBM, VIDEO_1440P_WEBM_60FPS_HDR, VIDEO_1440P_WEBM_60FPS, VIDEO_2160P_WEBM, VIDEO_2160P_WEBM_60FPS_HDR, VIDEO_2160P_WEBM_60FPS);
        sOrderedITagsWEBM = listAsList2;
        sITagsContainer = Arrays.asList(listAsList, listAsList2);
    }

    public static boolean belongsToType(String str, String str2) {
        return str.equals(getRealType(str2));
    }

    public static int compare(String str, String str2) {
        for (List<String> list : sITagsContainer) {
            int iIndexOf = list.indexOf(str);
            int iIndexOf2 = list.indexOf(str2);
            if (iIndexOf != -1 && iIndexOf2 != -1) {
                return iIndexOf - iIndexOf2;
            }
        }
        return 99;
    }

    public static String getAudioRateByTag(String str) {
        str.getClass();
        switch (str) {
            case "139":
                return "22050";
            case "249":
            case "250":
            case "251":
                return "48000";
            default:
                return "44100";
        }
    }

    private static String getRealType(String str) {
        return sOrderedITagsAVC.contains(str) ? AVC : "VP9";
    }

    public static boolean belongsToType(String str, int i10) {
        return str.equals(getRealType(String.valueOf(i10)));
    }
}
