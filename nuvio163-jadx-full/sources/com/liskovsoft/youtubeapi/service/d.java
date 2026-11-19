package com.liskovsoft.youtubeapi.service;

import java.util.concurrent.Callable;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class d implements Callable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7739b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ YouTubeContentService f7740c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ String f7741e;

    public /* synthetic */ d(YouTubeContentService youTubeContentService, String str, int i10) {
        this.f7739b = i10;
        this.f7740c = youTubeContentService;
        this.f7741e = str;
    }

    @Override // java.util.concurrent.Callable
    public final Object call() {
        switch (this.f7739b) {
            case 0:
                return this.f7740c.lambda$getGroupObserve$5(this.f7741e);
            case 1:
                return this.f7740c.lambda$getSearchObserve$0(this.f7741e);
            case 2:
                return this.f7740c.lambda$getChannelSortingOptionsObserve$16(this.f7741e);
            default:
                return this.f7740c.lambda$getSearchTagsObserve$2(this.f7741e);
        }
    }
}
