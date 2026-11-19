package com.liskovsoft.youtubeapi.service;

import com.liskovsoft.mediaserviceinterfaces.data.MediaItem;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class m implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7771b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f7772c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ String f7773e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ Object f7774i;

    public /* synthetic */ m(int i10, Object obj, Object obj2, String str) {
        this.f7771b = i10;
        this.f7772c = obj;
        this.f7773e = str;
        this.f7774i = obj2;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f7771b) {
            case 0:
                ((YouTubeMediaItemService) this.f7772c).lambda$createPlaylistObserve$31(this.f7773e, (MediaItem) this.f7774i);
                break;
            case 1:
                ((YouTubeMediaItemService) this.f7772c).lambda$addToPlaylistObserve$23(this.f7773e, (MediaItem) this.f7774i);
                break;
            default:
                ((YouTubeRemoteControlService) this.f7772c).lambda$postSubtitleChangeObserve$5(this.f7773e, (String) this.f7774i);
                break;
        }
    }
}
