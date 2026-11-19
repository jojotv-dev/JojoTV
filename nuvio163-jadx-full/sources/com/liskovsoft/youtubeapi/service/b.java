package com.liskovsoft.youtubeapi.service;

import java.util.concurrent.Callable;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class b implements Callable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7734b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ String f7735c;

    public /* synthetic */ b(String str, int i10) {
        this.f7734b = i10;
        this.f7735c = str;
    }

    @Override // java.util.concurrent.Callable
    public final Object call() {
        switch (this.f7734b) {
            case 0:
                return YouTubeCommentsService.getCommentsObserve$lambda$0(this.f7735c);
            case 1:
                return YouTubeMediaItemService.lambda$getUnlocalizedTitleObserve$37(this.f7735c);
            default:
                return YouTubeMediaItemService.lambda$getDislikeDataObserve$36(this.f7735c);
        }
    }
}
