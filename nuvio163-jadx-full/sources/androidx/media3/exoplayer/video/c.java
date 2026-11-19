package androidx.media3.exoplayer.video;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class c implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3367b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f3368c;

    public /* synthetic */ c(Object obj, int i10) {
        this.f3367b = i10;
        this.f3368c = obj;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3367b) {
            case 0:
                ((DefaultVideoSink) this.f3368c).lambda$handleInputFrame$2();
                break;
            default:
                ((PlaybackVideoGraphWrapper) this.f3368c).lambda$flush$1();
                break;
        }
    }
}
