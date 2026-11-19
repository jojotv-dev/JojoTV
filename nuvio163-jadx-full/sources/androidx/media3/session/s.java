package androidx.media3.session;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class s implements MediaControllerImplBase.RemoteSessionTask {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3668b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ MediaControllerImplBase f3669c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ int f3670e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ int f3671i;

    public /* synthetic */ s(MediaControllerImplBase mediaControllerImplBase, int i10, int i11, int i12) {
        this.f3668b = i12;
        this.f3669c = mediaControllerImplBase;
        this.f3670e = i10;
        this.f3671i = i11;
    }

    @Override // androidx.media3.session.MediaControllerImplBase.RemoteSessionTask
    public final void run(IMediaSession iMediaSession, int i10) {
        switch (this.f3668b) {
            case 0:
                this.f3669c.lambda$moveMediaItem$37(this.f3670e, this.f3671i, iMediaSession, i10);
                break;
            case 1:
                this.f3669c.lambda$removeMediaItems$35(this.f3670e, this.f3671i, iMediaSession, i10);
                break;
            default:
                this.f3669c.lambda$setDeviceVolume$53(this.f3670e, this.f3671i, iMediaSession, i10);
                break;
        }
    }
}
