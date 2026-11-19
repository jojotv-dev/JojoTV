package androidx.media3.session;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class l0 implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3579b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ MediaControllerImplBase f3580c;

    public /* synthetic */ l0(MediaControllerImplBase mediaControllerImplBase, int i10) {
        this.f3579b = i10;
        this.f3580c = mediaControllerImplBase;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3579b) {
            case 0:
                this.f3580c.lambda$release$4();
                break;
            default:
                this.f3580c.lambda$setFutureResult$105();
                break;
        }
    }
}
