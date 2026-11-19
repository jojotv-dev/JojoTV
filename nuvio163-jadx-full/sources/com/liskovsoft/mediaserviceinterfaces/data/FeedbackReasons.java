package com.liskovsoft.mediaserviceinterfaces.data;

import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public interface FeedbackReasons {

    public interface FeedbackItem {
        String getTitle();

        String getToken();
    }

    List<FeedbackItem> getItems();

    String getTitle();
}
