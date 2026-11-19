package com.liskovsoft.mediaserviceinterfaces.data;

import eh.c;
import java.io.InputStream;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public interface MediaItemFormatInfo extends FormatInfoProvision {

    public interface ClientInfo {
        String getClientName();

        String getClientVersion();

        String getOsName();

        String getOsVersion();

        String getUserAgent();
    }

    boolean containsDashFormats();

    boolean containsDashUrl();

    boolean containsHlsUrl();

    boolean containsMedia();

    boolean containsSabrFormats();

    boolean containsUrlFormats();

    InputStream createMpdStream();

    c createMpdStreamObservable();

    MediaItemStoryboard createStoryboard();

    List<String> createUrlList();

    List<MediaFormat> getAdaptiveFormats();

    String getAuthor();

    String getChannelId();

    ClientInfo getClientInfo();

    String getDashManifestUrl();

    String getDescription();

    String getHlsManifestUrl();

    String getLengthSeconds();

    String getPaidContentText();

    String getPlayabilityReason();

    String getPoToken();

    int getSegmentDurationUs();

    String getServerAbrStreamingUrl();

    int getStartSegmentNum();

    long getStartTimeMs();

    String getStartTimestamp();

    List<MediaSubtitle> getSubtitles();

    String getTitle();

    String getUploadDate();

    List<MediaFormat> getUrlFormats();

    String getVideoId();

    String getVideoPlaybackUstreamerConfig();

    String getViewCount();

    String getVisitorCookie();

    float getVolumeLevel();

    boolean hasExtendedHlsFormats();

    boolean isLive();

    boolean isLiveContent();

    boolean isLiveDvrEnabled();

    boolean isStreamSeekable();

    boolean isUnknownError();

    boolean isUnplayable();
}
