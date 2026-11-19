package com.liskovsoft.youtubeapi.service;

import com.liskovsoft.mediaserviceinterfaces.data.MediaItem;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class l implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7768b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ YouTubeMediaItemService f7769c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ MediaItem f7770e;

    public /* synthetic */ l(YouTubeMediaItemService youTubeMediaItemService, MediaItem mediaItem, int i10) {
        this.f7768b = i10;
        this.f7769c = youTubeMediaItemService;
        this.f7770e = mediaItem;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f7768b) {
            case 0:
                this.f7769c.lambda$removeDislikeObserve$17(this.f7770e);
                break;
            case 1:
                this.f7769c.lambda$unsubscribeObserve$12(this.f7770e);
                break;
            case 2:
                this.f7769c.lambda$setDislikeObserve$16(this.f7770e);
                break;
            case 3:
                this.f7769c.lambda$savePlaylistObserve$28(this.f7770e);
                break;
            case 4:
                this.f7769c.lambda$setLikeObserve$14(this.f7770e);
                break;
            case 5:
                this.f7769c.lambda$subscribeObserve$10(this.f7770e);
                break;
            default:
                this.f7769c.lambda$removeLikeObserve$15(this.f7770e);
                break;
        }
    }
}
