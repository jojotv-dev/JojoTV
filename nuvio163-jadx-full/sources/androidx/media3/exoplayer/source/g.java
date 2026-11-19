package androidx.media3.exoplayer.source;

import androidx.media3.common.util.Consumer;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class g implements Consumer {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3276b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ MediaSourceEventListener.EventDispatcher f3277c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ LoadEventInfo f3278e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ MediaLoadData f3279i;

    public /* synthetic */ g(MediaSourceEventListener.EventDispatcher eventDispatcher, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, int i10) {
        this.f3276b = i10;
        this.f3277c = eventDispatcher;
        this.f3278e = loadEventInfo;
        this.f3279i = mediaLoadData;
    }

    @Override // androidx.media3.common.util.Consumer, ih.b
    public final void accept(Object obj) {
        switch (this.f3276b) {
            case 0:
                this.f3277c.lambda$loadCompleted$1(this.f3278e, this.f3279i, (MediaSourceEventListener) obj);
                break;
            default:
                this.f3277c.lambda$loadCanceled$2(this.f3278e, this.f3279i, (MediaSourceEventListener) obj);
                break;
        }
    }
}
