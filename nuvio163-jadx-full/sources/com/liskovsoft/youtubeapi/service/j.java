package com.liskovsoft.youtubeapi.service;

import com.liskovsoft.mediaserviceinterfaces.data.MediaItem;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class j implements eh.e {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7760b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f7761c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Object f7762e;

    public /* synthetic */ j(Object obj, Object obj2, int i10) {
        this.f7760b = i10;
        this.f7761c = obj;
        this.f7762e = obj2;
    }

    @Override // eh.e
    public final void a(eh.d dVar) {
        switch (this.f7760b) {
            case 0:
                ((YouTubeLiveChatService) this.f7761c).lambda$openLiveChatObserve$0((String) this.f7762e, dVar);
                break;
            case 1:
                ((YouTubeMediaItemService) this.f7761c).lambda$getMetadataObserve$5((MediaItem) this.f7762e, dVar);
                break;
            default:
                ((YouTubeMediaItemService) this.f7761c).lambda$getDeArrowDataObserve$35((List) this.f7762e, dVar);
                break;
        }
    }
}
