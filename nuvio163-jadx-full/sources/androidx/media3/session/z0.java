package androidx.media3.session;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class z0 implements MediaControllerImplBase.RemoteSessionTask {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3747b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ MediaControllerImplBase.SurfaceCallback f3748c;

    public /* synthetic */ z0(MediaControllerImplBase.SurfaceCallback surfaceCallback, int i10) {
        this.f3747b = i10;
        this.f3748c = surfaceCallback;
    }

    @Override // androidx.media3.session.MediaControllerImplBase.RemoteSessionTask
    public final void run(IMediaSession iMediaSession, int i10) {
        switch (this.f3747b) {
            case 0:
                this.f3748c.lambda$surfaceCreated$0(iMediaSession, i10);
                break;
            case 1:
                this.f3748c.lambda$surfaceDestroyed$1(iMediaSession, i10);
                break;
            case 2:
                this.f3748c.lambda$onSurfaceTextureAvailable$2(iMediaSession, i10);
                break;
            default:
                this.f3748c.lambda$onSurfaceTextureDestroyed$3(iMediaSession, i10);
                break;
        }
    }
}
