package androidx.media3.exoplayer.drm;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class e implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3091b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ DrmSessionEventListener.EventDispatcher f3092c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ DrmSessionEventListener f3093e;

    public /* synthetic */ e(DrmSessionEventListener.EventDispatcher eventDispatcher, DrmSessionEventListener drmSessionEventListener, int i10) {
        this.f3091b = i10;
        this.f3092c = eventDispatcher;
        this.f3093e = drmSessionEventListener;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3091b) {
            case 0:
                this.f3092c.lambda$drmKeysRemoved$4(this.f3093e);
                break;
            case 1:
                this.f3092c.lambda$drmKeysRestored$3(this.f3093e);
                break;
            case 2:
                this.f3092c.lambda$drmKeysLoaded$1(this.f3093e);
                break;
            default:
                this.f3092c.lambda$drmSessionReleased$5(this.f3093e);
                break;
        }
    }
}
