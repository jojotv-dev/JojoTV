package com.liskovsoft.youtubeapi.service;

import java.util.Set;
import java.util.concurrent.Callable;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class g implements Callable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7748b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ String f7749c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Object f7750e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ Object f7751i;

    public /* synthetic */ g(int i10, Object obj, Object obj2, String str) {
        this.f7748b = i10;
        this.f7751i = obj;
        this.f7749c = str;
        this.f7750e = obj2;
    }

    @Override // java.util.concurrent.Callable
    public final Object call() {
        switch (this.f7748b) {
            case 0:
                return ((YouTubeContentService) this.f7751i).lambda$getChannelSearchObserve$17(this.f7749c, (String) this.f7750e);
            case 1:
                return ((YouTubeMediaItemService) this.f7751i).lambda$getFormatInfoObserve$2(this.f7749c, (String) this.f7750e);
            default:
                return ((YouTubeMediaItemService) this.f7751i).lambda$getSponsorSegmentsObserve$33(this.f7749c, (Set) this.f7750e);
        }
    }
}
