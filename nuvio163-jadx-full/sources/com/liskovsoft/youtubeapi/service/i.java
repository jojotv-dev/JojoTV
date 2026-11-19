package com.liskovsoft.youtubeapi.service;

import com.liskovsoft.mediaserviceinterfaces.data.FeedbackEndpoint;
import com.liskovsoft.mediaserviceinterfaces.data.MediaGroup;
import com.liskovsoft.mediaserviceinterfaces.data.MediaItem;
import java.util.concurrent.Callable;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class i implements Callable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7754b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f7755c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Object f7756e;

    public /* synthetic */ i(Object obj, Object obj2, int i10) {
        this.f7754b = i10;
        this.f7755c = obj;
        this.f7756e = obj2;
    }

    @Override // java.util.concurrent.Callable
    public final Object call() {
        switch (this.f7754b) {
            case 0:
                return ((YouTubeContentService) this.f7755c).lambda$getGroupObserve$4((MediaItem) this.f7756e);
            case 1:
                return ((YouTubeContentService) this.f7755c).lambda$continueGroupObserve$18((MediaGroup) this.f7756e);
            case 2:
                return ((YouTubeContentService) this.f7755c).lambda$getRssFeedObserve$3((String[]) this.f7756e);
            default:
                return ((YouTubeMediaItemService) this.f7755c).lambda$getFeedbackTokensObserve$20((FeedbackEndpoint) this.f7756e);
        }
    }
}
