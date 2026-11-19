package com.liskovsoft.youtubeapi.track;

import androidx.media3.exoplayer.upstream.CmcdConfiguration;
import aq.f;
import aq.t;
import com.liskovsoft.googlecommon.common.converters.jsonpath.WithJsonPath;
import com.liskovsoft.youtubeapi.track.models.WatchTimeEmptyResult;

/* JADX INFO: loaded from: classes4.dex */
@WithJsonPath
public interface TrackingApi {
    @f("https://www.youtube.com/api/stats/playback?ns=yt&ver=2")
    xp.f<WatchTimeEmptyResult> createWatchRecord(@t("docid") String str, @t("len") float f10, @t("cmt") float f11, @t("cpn") String str2, @t("ei") String str3, @t("vm") String str4, @t("of") String str5);

    @f("https://www.youtube.com/api/stats/playback?ns=yt&ver=2&final=1")
    xp.f<WatchTimeEmptyResult> createWatchRecordShort(@t("docid") String str, @t("cpn") String str2, @t("ei") String str3, @t("vm") String str4, @t("of") String str5);

    @f("https://www.youtube.com/api/stats/watchtime?ns=yt&ver=2")
    xp.f<WatchTimeEmptyResult> updateWatchTime(@t("docid") String str, @t("len") float f10, @t(CmcdConfiguration.KEY_STREAM_TYPE) float f11, @t("et") float f12, @t("cmt") float f13, @t("cpn") String str2, @t("ei") String str3, @t("vm") String str4, @t("of") String str5);

    @f("https://www.youtube.com/api/stats/watchtime?ns=yt&ver=2&final=1")
    xp.f<WatchTimeEmptyResult> updateWatchTimeShort(@t("docid") String str, @t(CmcdConfiguration.KEY_STREAM_TYPE) float f10, @t("et") float f11, @t("cpn") String str2, @t("ei") String str3, @t("vm") String str4, @t("of") String str5);
}
