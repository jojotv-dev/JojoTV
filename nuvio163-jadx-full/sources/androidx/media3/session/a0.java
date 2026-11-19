package androidx.media3.session;

import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class a0 implements MediaControllerImplBase.RemoteSessionTask {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3444b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ MediaControllerImplBase f3445c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ List f3446e;

    public /* synthetic */ a0(int i10, List list, MediaControllerImplBase mediaControllerImplBase) {
        this.f3444b = i10;
        this.f3445c = mediaControllerImplBase;
        this.f3446e = list;
    }

    @Override // androidx.media3.session.MediaControllerImplBase.RemoteSessionTask
    public final void run(IMediaSession iMediaSession, int i10) {
        switch (this.f3444b) {
            case 0:
                this.f3445c.lambda$addMediaItems$32(this.f3446e, iMediaSession, i10);
                break;
            default:
                this.f3445c.lambda$setMediaItems$25(this.f3446e, iMediaSession, i10);
                break;
        }
    }
}
