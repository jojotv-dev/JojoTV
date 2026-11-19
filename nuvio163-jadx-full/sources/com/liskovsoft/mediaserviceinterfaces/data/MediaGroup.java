package com.liskovsoft.mediaserviceinterfaces.data;

import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public interface MediaGroup {
    public static final int TYPE_BLOCKED_CHANNELS = 22;
    public static final int TYPE_CHANNEL = 10;
    public static final int TYPE_CHANNEL_UPLOADS = 12;
    public static final int TYPE_GAMING = 7;
    public static final int TYPE_HISTORY = 3;
    public static final int TYPE_HOME = 0;
    public static final int TYPE_KIDS_HOME = 13;
    public static final int TYPE_LIVE = 19;
    public static final int TYPE_MOVIES = 18;
    public static final int TYPE_MUSIC = 5;
    public static final int TYPE_MY_VIDEOS = 20;
    public static final int TYPE_NEWS = 6;
    public static final int TYPE_NOTIFICATIONS = 16;
    public static final int TYPE_PLAYBACK_QUEUE = 21;
    public static final int TYPE_RECOMMENDED = 2;
    public static final int TYPE_SEARCH = 1;
    public static final int TYPE_SETTINGS = 11;
    public static final int TYPE_SHORTS = 15;
    public static final int TYPE_SPORTS = 17;
    public static final int TYPE_SUBSCRIPTIONS = 4;
    public static final int TYPE_SUGGESTIONS = 9;
    public static final int TYPE_TRENDING = 14;
    public static final int TYPE_UNDEFINED = -1;
    public static final int TYPE_USER_PLAYLISTS = 8;

    String getChannelId();

    String getChannelUrl();

    List<MediaItem> getMediaItems();

    String getNextPageKey();

    String getParams();

    String getReloadPageKey();

    String getTitle();

    int getType();

    boolean isEmpty();
}
