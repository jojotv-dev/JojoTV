package com.liskovsoft.mediaserviceinterfaces.data;

import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public interface MediaItemMetadata {
    public static final int LIKE_STATUS_DISLIKE = 2;
    public static final int LIKE_STATUS_INDIFFERENT = 0;
    public static final int LIKE_STATUS_LIKE = 1;

    String getAuthor();

    String getAuthorImageUrl();

    String getBadgeText();

    String getChannelId();

    List<ChapterItem> getChapters();

    String getCommentsKey();

    String getDescription();

    String getDislikeCount();

    long getDurationMs();

    String getLikeCount();

    int getLikeStatus();

    String getLiveChatKey();

    default String getNewestCommentsKey() {
        return null;
    }

    MediaItem getNextVideo();

    List<NotificationState> getNotificationStates();

    String getParams();

    int getPercentWatched();

    PlaylistInfo getPlaylistInfo();

    String getPublishedDate();

    CharSequence getSecondTitle();

    MediaItem getShuffleVideo();

    String getSubscriberCount();

    List<MediaGroup> getSuggestions();

    String getTitle();

    String getVideoId();

    String getViewCount();

    boolean isLive();

    boolean isSubscribed();

    boolean isUpcoming();
}
