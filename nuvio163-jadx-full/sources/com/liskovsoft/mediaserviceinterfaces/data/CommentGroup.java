package com.liskovsoft.mediaserviceinterfaces.data;

import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public interface CommentGroup {
    List<CommentItem> getComments();

    String getNextCommentsKey();
}
