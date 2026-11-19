package com.liskovsoft.youtubeapi.service.data;

import com.liskovsoft.mediaserviceinterfaces.data.MediaSubtitle;
import com.liskovsoft.youtubeapi.videoinfo.models.CaptionTrack;

/* JADX INFO: loaded from: classes4.dex */
public class YouTubeMediaSubtitle implements MediaSubtitle {
    private String mBaseUrl;
    private String mCodecs;
    private boolean mIsTranslatable;
    private String mLanguageCode;
    private String mMimeType;
    private String mName;
    private String mType;
    private String mVssId;

    public static MediaSubtitle from(CaptionTrack captionTrack) {
        YouTubeMediaSubtitle youTubeMediaSubtitle = new YouTubeMediaSubtitle();
        youTubeMediaSubtitle.mBaseUrl = captionTrack.getBaseUrl();
        youTubeMediaSubtitle.mIsTranslatable = captionTrack.isTranslatable();
        youTubeMediaSubtitle.mLanguageCode = captionTrack.getLanguageCode();
        youTubeMediaSubtitle.mVssId = captionTrack.getVssId();
        youTubeMediaSubtitle.mName = captionTrack.getName();
        youTubeMediaSubtitle.mType = captionTrack.getType();
        youTubeMediaSubtitle.mMimeType = captionTrack.getMimeType();
        youTubeMediaSubtitle.mCodecs = captionTrack.getCodecs();
        return youTubeMediaSubtitle;
    }

    @Override // com.liskovsoft.mediaserviceinterfaces.data.MediaSubtitle
    public String getBaseUrl() {
        return this.mBaseUrl;
    }

    @Override // com.liskovsoft.mediaserviceinterfaces.data.MediaSubtitle
    public String getCodecs() {
        return this.mCodecs;
    }

    @Override // com.liskovsoft.mediaserviceinterfaces.data.MediaSubtitle
    public String getLanguageCode() {
        return this.mLanguageCode;
    }

    @Override // com.liskovsoft.mediaserviceinterfaces.data.MediaSubtitle
    public String getMimeType() {
        return this.mMimeType;
    }

    @Override // com.liskovsoft.mediaserviceinterfaces.data.MediaSubtitle
    public String getName() {
        return this.mName;
    }

    @Override // com.liskovsoft.mediaserviceinterfaces.data.MediaSubtitle
    public String getType() {
        return this.mType;
    }

    @Override // com.liskovsoft.mediaserviceinterfaces.data.MediaSubtitle
    public String getVssId() {
        return this.mVssId;
    }

    @Override // com.liskovsoft.mediaserviceinterfaces.data.MediaSubtitle
    public boolean isTranslatable() {
        return this.mIsTranslatable;
    }
}
