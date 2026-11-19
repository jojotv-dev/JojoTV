package androidx.media3.session;

import android.view.Surface;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class x implements MediaControllerImplBase.RemoteSessionTask {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3726b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ MediaControllerImplBase f3727c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Surface f3728e;

    public /* synthetic */ x(MediaControllerImplBase mediaControllerImplBase, Surface surface, int i10) {
        this.f3726b = i10;
        this.f3727c = mediaControllerImplBase;
        this.f3728e = surface;
    }

    @Override // androidx.media3.session.MediaControllerImplBase.RemoteSessionTask
    public final void run(IMediaSession iMediaSession, int i10) {
        switch (this.f3726b) {
            case 0:
                this.f3727c.lambda$setVideoSurface$70(this.f3728e, iMediaSession, i10);
                break;
            default:
                this.f3727c.lambda$setVideoSurfaceHolder$71(this.f3728e, iMediaSession, i10);
                break;
        }
    }
}
