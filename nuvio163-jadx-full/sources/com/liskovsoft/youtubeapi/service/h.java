package com.liskovsoft.youtubeapi.service;

import java.util.concurrent.Callable;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class h implements Callable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7752b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ YouTubeContentService f7753c;

    public /* synthetic */ h(YouTubeContentService youTubeContentService, int i10) {
        this.f7752b = i10;
        this.f7753c = youTubeContentService;
    }

    @Override // java.util.concurrent.Callable
    public final Object call() {
        switch (this.f7752b) {
            case 0:
                return this.f7753c.getHistory();
            case 1:
                return this.f7753c.getSubscriptions();
            case 2:
                return this.f7753c.getRecommended();
            case 3:
                return this.f7753c.getSubscribedChannelsByName();
            case 4:
                return this.f7753c.getPlaylists();
            case 5:
                return this.f7753c.getSubscribedChannelsByNewContent();
            case 6:
                return this.f7753c.getSubscribedChannelsByLastViewed();
            default:
                return this.f7753c.getSubscribedChannels();
        }
    }
}
