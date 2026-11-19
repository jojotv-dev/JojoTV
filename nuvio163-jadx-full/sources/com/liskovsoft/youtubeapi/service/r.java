package com.liskovsoft.youtubeapi.service;

import com.liskovsoft.mediaserviceinterfaces.data.MediaItem;
import java.util.concurrent.Callable;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class r implements Callable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7789b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ YouTubeMediaItemService f7790c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ MediaItem f7791e;

    public /* synthetic */ r(YouTubeMediaItemService youTubeMediaItemService, MediaItem mediaItem, int i10) {
        this.f7789b = i10;
        this.f7790c = youTubeMediaItemService;
        this.f7791e = mediaItem;
    }

    @Override // java.util.concurrent.Callable
    public final Object call() {
        switch (this.f7789b) {
            case 0:
                return this.f7790c.lambda$getFormatInfoObserve$0(this.f7791e);
            default:
                return this.f7790c.lambda$getStoryboardObserve$3(this.f7791e);
        }
    }
}
