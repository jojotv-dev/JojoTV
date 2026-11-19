package com.liskovsoft.youtubeapi.service;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class o implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7778b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ YouTubeMediaItemService f7779c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ String f7780e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ String f7781i;

    public /* synthetic */ o(YouTubeMediaItemService youTubeMediaItemService, String str, String str2, int i10) {
        this.f7778b = i10;
        this.f7779c = youTubeMediaItemService;
        this.f7780e = str;
        this.f7781i = str2;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f7778b) {
            case 0:
                this.f7779c.lambda$addToPlaylistObserve$22(this.f7780e, this.f7781i);
                break;
            case 1:
                this.f7779c.lambda$renamePlaylistObserve$25(this.f7780e, this.f7781i);
                break;
            case 2:
                this.f7779c.lambda$createPlaylistObserve$30(this.f7780e, this.f7781i);
                break;
            default:
                this.f7779c.lambda$removeFromPlaylistObserve$24(this.f7780e, this.f7781i);
                break;
        }
    }
}
