package androidx.media3.session;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class z1 implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3749b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ MediaSessionImpl f3750c;

    public /* synthetic */ z1(MediaSessionImpl mediaSessionImpl, int i10) {
        this.f3749b = i10;
        this.f3750c = mediaSessionImpl;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3749b) {
            case 0:
                this.f3750c.lambda$onNotificationRefreshRequired$19();
                break;
            case 1:
                this.f3750c.lambda$release$2();
                break;
            case 2:
                this.f3750c.notifyPeriodicSessionPositionInfoChangesOnHandler();
                break;
            default:
                this.f3750c.schedulePeriodicSessionPositionInfoChanges();
                break;
        }
    }
}
