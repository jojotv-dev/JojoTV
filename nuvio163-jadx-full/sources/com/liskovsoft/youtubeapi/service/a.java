package com.liskovsoft.youtubeapi.service;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class a implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7732b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ String f7733c;

    public /* synthetic */ a(String str, int i10) {
        this.f7732b = i10;
        this.f7733c = str;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f7732b) {
            case 0:
                YouTubeCommentsService.toggleDislikeObserve$lambda$0(this.f7733c);
                break;
            default:
                YouTubeCommentsService.toggleLikeObserve$lambda$0(this.f7733c);
                break;
        }
    }
}
