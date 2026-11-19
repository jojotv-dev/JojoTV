package com.liskovsoft.mediaserviceinterfaces.data;

/* JADX INFO: loaded from: classes4.dex */
public interface CommentItem {
    String getAuthorName();

    String getAuthorPhoto();

    String getId();

    String getLikeCount();

    String getMessage();

    String getNestedCommentsKey();

    String getPublishedDate();

    String getReplyCount();

    boolean isEmpty();

    boolean isLiked();
}
