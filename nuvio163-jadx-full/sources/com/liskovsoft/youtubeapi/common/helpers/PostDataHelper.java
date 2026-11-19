package com.liskovsoft.youtubeapi.common.helpers;

import com.liskovsoft.googlecommon.common.locale.LocaleManager;
import com.liskovsoft.youtubeapi.app.AppService;

/* JADX INFO: loaded from: classes4.dex */
public class PostDataHelper {
    public static String createQuery(AppClient appClient, String str) {
        return createQuery(appClient.getBrowseTemplate(), str);
    }

    public static String createQueryAndroid(String str) {
        return createQuery(AppClient.ANDROID.getBrowseTemplate(), str);
    }

    public static String createQueryKids(String str) {
        return createQuery(AppClient.TV_KIDS.getBrowseTemplate(), str);
    }

    public static String createQueryMWeb(String str) {
        return createQuery(AppClient.MWEB.getBrowseTemplate(), str);
    }

    public static String createQueryRemix(String str) {
        return createQuery(AppClient.WEB_MUSIC.getBrowseTemplate(), str);
    }

    public static String createQueryTV(String str) {
        return createQuery(AppClient.TV.getBrowseTemplate(), str);
    }

    public static String createQueryTVLegacy(String str) {
        return createQuery(AppClient.TV_LEGACY.getBrowseTemplate(), str);
    }

    public static String createQueryTV_UA(String str) {
        return createQuery(AppClient.TV.getBrowseTemplate(), null, str, "uk", "UA");
    }

    public static String createQueryWeb(String str) {
        return createQuery(AppClient.WEB.getBrowseTemplate(), str);
    }

    public static String createQuery(String str, String str2) {
        return createQuery(str, null, str2, null, null);
    }

    public static String createQuery(AppClient appClient, String str, String str2) {
        return createQuery(appClient.getBaseTemplate(), str, str2);
    }

    public static String createQuery(String str, String str2, String str3) {
        return createQuery(str, str2, str3, null, null);
    }

    private static String createQuery(String str, String str2, String str3, String str4, String str5) {
        LocaleManager localeManagerInstance = LocaleManager.instance();
        AppService appServiceInstance = AppService.instance();
        if (str4 == null) {
            str4 = localeManagerInstance.getLanguage();
        }
        String str6 = str4;
        if (str5 == null) {
            str5 = localeManagerInstance.getCountry();
        }
        return String.format(str, str6, str5, Integer.valueOf(localeManagerInstance.getUtcOffsetMinutes()), appServiceInstance.getVisitorData(), str2 != null ? str2 : "", str3 != null ? str3 : "");
    }
}
