package androidx.media3.exoplayer.drm;

import androidx.media3.common.util.Consumer;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class a implements Consumer {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3084b;

    public /* synthetic */ a(int i10) {
        this.f3084b = i10;
    }

    @Override // androidx.media3.common.util.Consumer, ih.b
    public void accept(Object obj) {
        DrmSessionEventListener.EventDispatcher eventDispatcher = (DrmSessionEventListener.EventDispatcher) obj;
        switch (this.f3084b) {
            case 0:
                eventDispatcher.drmSessionAcquired(3);
                break;
            case 1:
                eventDispatcher.drmKeysRemoved();
                break;
            case 2:
                eventDispatcher.drmKeysLoaded();
                break;
            default:
                eventDispatcher.drmKeysRestored();
                break;
        }
    }
}
