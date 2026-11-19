package androidx.media3.session;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class c1 implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3479b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ MediaControllerImplLegacy f3480c;

    public /* synthetic */ c1(MediaControllerImplLegacy mediaControllerImplLegacy, int i10) {
        this.f3479b = i10;
        this.f3480c = mediaControllerImplLegacy;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3479b) {
            case 0:
                this.f3480c.lambda$connectToSession$2();
                break;
            default:
                this.f3480c.lambda$connectToService$3();
                break;
        }
    }
}
