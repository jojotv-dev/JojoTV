package androidx.media3.exoplayer.audio;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class f implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3043b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ AudioRendererEventListener.EventDispatcher f3044c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Exception f3045e;

    public /* synthetic */ f(AudioRendererEventListener.EventDispatcher eventDispatcher, Exception exc, int i10) {
        this.f3043b = i10;
        this.f3044c = eventDispatcher;
        this.f3045e = exc;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3043b) {
            case 0:
                this.f3044c.lambda$audioCodecError$9(this.f3045e);
                break;
            default:
                this.f3044c.lambda$audioSinkError$8(this.f3045e);
                break;
        }
    }
}
