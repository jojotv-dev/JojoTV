package com.liskovsoft.mediaserviceinterfaces.data;

/* JADX INFO: loaded from: classes4.dex */
public interface MediaItem {
    public static final int TYPE_CHANNEL = 2;
    public static final int TYPE_MUSIC = 1;
    public static final int TYPE_PLAYLIST = 3;
    public static final int TYPE_UNDEFINED = -1;
    public static final int TYPE_VIDEO = 0;

    String getAudioChannelConfig();

    String getAuthor();

    String getBackgroundImageUrl();

    String getBadgeText();

    String getCardImageUrl();

    String getChannelId();

    String getClickTrackingParams();

    String getContentType();

    long getDurationMs();

    FeedbackEndpoint getFeedbackEndpoint();

    String getFeedbackToken();

    String getFeedbackToken2();

    int getHeight();

    int getId();

    String getParams();

    int getPercentWatched();

    String getPlaylistId();

    int getPlaylistIndex();

    String getProductionDate();

    long getPublishedDate();

    String getPurchasePrice();

    double getRatingScore();

    int getRatingStyle();

    String getReloadPageKey();

    String getRentalPrice();

    String getSearchQuery();

    CharSequence getSecondTitle();

    int getStartTimeSeconds();

    String getTitle();

    int getType();

    String getVideoId();

    String getVideoPreviewUrl();

    int getWidth();

    boolean hasNewContent();

    boolean hasUploads();

    boolean isLive();

    boolean isMovie();

    boolean isShorts();

    boolean isUpcoming();
}
