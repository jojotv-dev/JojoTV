package com.liskovsoft.youtubeapi.service;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class e implements eh.e {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7742b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ YouTubeContentService f7743c;

    public /* synthetic */ e(YouTubeContentService youTubeContentService, int i10) {
        this.f7742b = i10;
        this.f7743c = youTubeContentService;
    }

    @Override // eh.e
    public final void a(eh.d dVar) {
        switch (this.f7742b) {
            case 0:
                this.f7743c.lambda$getLiveObserve$11(dVar);
                break;
            case 1:
                this.f7743c.lambda$getPlaylistRowsObserve$19(dVar);
                break;
            case 2:
                this.f7743c.lambda$getGamingObserve$14(dVar);
                break;
            case 3:
                this.f7743c.lambda$getSportsObserve$10(dVar);
                break;
            case 4:
                this.f7743c.lambda$getShortsObserve$8(dVar);
                break;
            case 5:
                this.f7743c.lambda$getNewsObserve$13(dVar);
                break;
            case 6:
                this.f7743c.lambda$getKidsHomeObserve$9(dVar);
                break;
            case 7:
                this.f7743c.lambda$getHomeObserve$6(dVar);
                break;
            case 8:
                this.f7743c.lambda$getTrendingObserve$7(dVar);
                break;
            default:
                this.f7743c.lambda$getMusicObserve$12(dVar);
                break;
        }
    }
}
