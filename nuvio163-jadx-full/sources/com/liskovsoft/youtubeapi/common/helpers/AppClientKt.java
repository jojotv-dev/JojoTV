package com.liskovsoft.youtubeapi.common.helpers;

import androidx.media3.container.NalUnitUtil;
import kotlin.Metadata;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0003\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0004\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0005\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0006\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0007\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\b\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\t\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\n\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000¨\u0006\u000b"}, d2 = {"JSON_POST_DATA_BASE", "", "POST_DATA_BROWSE_TV", "POST_DATA_BROWSE_TV_LEGACY", "POST_DATA_IOS_MODEL", "POST_DATA_ANDROID_OS", "POST_DATA_ANDROID_SDK", "POST_DATA_ANDROID_MODEL", "POST_DATA_BROWSER", "CLIENT_SCREEN_WATCH", "CLIENT_SCREEN_EMBED", "youtubeapi_release"}, k = 2, mv = {2, 3, 0}, xi = NalUnitUtil.H265_NAL_UNIT_TYPE_UNSPECIFIED)
public final class AppClientKt {
    private static final String CLIENT_SCREEN_EMBED = "EMBED";
    private static final String CLIENT_SCREEN_WATCH = "WATCH";
    private static final String JSON_POST_DATA_BASE = "{\"context\":{\"client\":{\"clientName\":\"%s\",\"clientVersion\":\"%s\",\"clientScreen\":\"%s\",\"userAgent\":\"%s\",%s\"acceptLanguage\":\"%%s\",\"acceptRegion\":\"%%s\",\"utcOffsetMinutes\":\"%%s\",\"visitorData\":\"%%s\"},%%s\"user\":{\"enableSafetyMode\":false,\"lockedSafetyMode\":false}},\"racyCheckOk\":true,\"contentCheckOk\":true,%%s}";
    private static final String POST_DATA_ANDROID_MODEL = "\"deviceModel\":\"%s\",\"deviceMake\":\"%s\",";
    private static final String POST_DATA_ANDROID_OS = "\"osName\":\"Android\",\"osVersion\":\"%s\",";
    private static final String POST_DATA_ANDROID_SDK = "\"androidSdkVersion\":\"%s\",";
    private static final String POST_DATA_BROWSER = "\"browserName\":\"%s\",\"browserVersion\":\"%s\",";
    private static final String POST_DATA_BROWSE_TV = "\"tvAppInfo\":{\"appQuality\":\"TV_APP_QUALITY_FULL_ANIMATION\",\"zylonLeftNav\":true},\"webpSupport\":false,\"animatedWebpSupport\":true,";
    private static final String POST_DATA_BROWSE_TV_LEGACY = "\"tvAppInfo\":{\"appQuality\":\"TV_APP_QUALITY_LIMITED_ANIMATION\",\"zylonLeftNav\":true},\"webpSupport\":false,\"animatedWebpSupport\":true,";
    private static final String POST_DATA_IOS_MODEL = "\"deviceModel\":\"%s\",\"osVersion\":\"%s\",";
}
