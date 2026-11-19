package com.liskovsoft.youtubeapi.innertube.utils;

import androidx.media3.container.NalUnitUtil;
import kotlin.Metadata;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0006\bÀ\u0002\u0018\u00002\u00020\u0001:\u0001\nB\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u000b"}, d2 = {"Lcom/liskovsoft/youtubeapi/innertube/utils/URLS;", "", "<init>", "()V", "YT_BASE", "", "YT_MUSIC_BASE", "YT_SUGGESTIONS", "YT_UPLOAD", "GOOGLE_SEARCH_BASE", "API", "youtubeapi_release"}, k = 1, mv = {2, 3, 0}, xi = NalUnitUtil.H265_NAL_UNIT_TYPE_UNSPECIFIED)
public final class URLS {
    public static final String GOOGLE_SEARCH_BASE = "https://www.google.com/";
    public static final URLS INSTANCE = new URLS();
    public static final String YT_BASE = "https://www.youtube.com";
    public static final String YT_MUSIC_BASE = "https://music.youtube.com";
    public static final String YT_SUGGESTIONS = "https://suggestqueries-clients6.youtube.com";
    public static final String YT_UPLOAD = "https://upload.youtube.com/";

    @Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\b\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006\r"}, d2 = {"Lcom/liskovsoft/youtubeapi/innertube/utils/URLS$API;", "", "<init>", "()V", "BASE", "", "PRODUCTION_1", "PRODUCTION_2", "STAGING", "RELEASE", "TEST", "CAMI", "UYTFE", "youtubeapi_release"}, k = 1, mv = {2, 3, 0}, xi = NalUnitUtil.H265_NAL_UNIT_TYPE_UNSPECIFIED)
    public static final class API {
        public static final String BASE = "https://youtubei.googleapis.com";
        public static final String CAMI = "http://cami-youtubei.sandbox.googleapis.com/youtubei/";
        public static final API INSTANCE = new API();
        public static final String PRODUCTION_1 = "https://www.youtube.com/youtubei/";
        public static final String PRODUCTION_2 = "https://youtubei.googleapis.com/youtubei/";
        public static final String RELEASE = "https://release-youtubei.sandbox.googleapis.com/youtubei/";
        public static final String STAGING = "https://green-youtubei.sandbox.googleapis.com/youtubei/";
        public static final String TEST = "https://test-youtubei.sandbox.googleapis.com/youtubei/";
        public static final String UYTFE = "https://uytfe.sandbox.google.com/youtubei/";

        private API() {
        }
    }

    private URLS() {
    }
}
