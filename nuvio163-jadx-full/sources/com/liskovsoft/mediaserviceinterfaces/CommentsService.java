package com.liskovsoft.mediaserviceinterfaces;

import eh.c;

/* JADX INFO: loaded from: classes4.dex */
public interface CommentsService {
    c getCommentsObserve(String str);

    c toggleDislikeObserve(String str);

    c toggleLikeObserve(String str);
}
