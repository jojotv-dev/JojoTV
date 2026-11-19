package com.liskovsoft.youtubeapi.service;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class p implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7782b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ YouTubeMediaItemService f7783c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ String f7784e;

    public /* synthetic */ p(YouTubeMediaItemService youTubeMediaItemService, String str, int i10) {
        this.f7782b = i10;
        this.f7783c = youTubeMediaItemService;
        this.f7784e = str;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f7782b) {
            case 0:
                this.f7783c.lambda$unsubscribeObserve$13(this.f7784e);
                break;
            case 1:
                this.f7783c.lambda$removePlaylistObserve$29(this.f7784e);
                break;
            case 2:
                this.f7783c.lambda$subscribeObserve$11(this.f7784e);
                break;
            case 3:
                this.f7783c.lambda$savePlaylistObserve$27(this.f7784e);
                break;
            default:
                this.f7783c.lambda$markAsNotInterestedObserve$18(this.f7784e);
                break;
        }
    }
}
