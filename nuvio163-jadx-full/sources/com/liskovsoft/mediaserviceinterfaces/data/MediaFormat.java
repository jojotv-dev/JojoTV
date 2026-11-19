package com.liskovsoft.mediaserviceinterfaces.data;

import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public interface MediaFormat extends Comparable<MediaFormat> {
    public static final int FORMAT_TYPE_DASH = 0;
    public static final int FORMAT_TYPE_REGULAR = 1;
    public static final int FORMAT_TYPE_SABR = 2;

    int getApproxDurationMs();

    String getAudioSamplingRate();

    String getAudioTrackId();

    String getBitrate();

    String getClen();

    String getFormat();

    int getFormatType();

    String getFps();

    List<String> getGlobalSegmentList();

    int getHeight();

    String getITag();

    String getIndex();

    String getInit();

    String getLanguage();

    String getLmt();

    int getMaxDvrDurationSec();

    String getMimeType();

    String getOtfInitUrl();

    String getOtfTemplateUrl();

    String getProjectionType();

    String getQuality();

    String getQualityLabel();

    List<String> getSegmentUrlList();

    String getSignature();

    String getSourceUrl();

    int getTargetDurationSec();

    String getUrl();

    int getWidth();

    String getXtags();

    boolean isDrc();

    boolean isOtf();
}
