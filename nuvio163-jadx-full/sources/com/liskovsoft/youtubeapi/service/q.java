package com.liskovsoft.youtubeapi.service;

import com.liskovsoft.mediaserviceinterfaces.data.MediaItem;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class q implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7785b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ YouTubeMediaItemService f7786c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ float f7787e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ Object f7788i;

    public /* synthetic */ q(YouTubeMediaItemService youTubeMediaItemService, Object obj, float f10, int i10) {
        this.f7785b = i10;
        this.f7786c = youTubeMediaItemService;
        this.f7788i = obj;
        this.f7787e = f10;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f7785b) {
            case 0:
                this.f7786c.lambda$updateHistoryPositionObserve$8((MediaItem) this.f7788i, this.f7787e);
                break;
            default:
                this.f7786c.lambda$updateHistoryPositionObserve$9((String) this.f7788i, this.f7787e);
                break;
        }
    }
}
