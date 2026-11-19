package androidx.media3.exoplayer.source;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class i implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3281b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ ProgressiveMediaPeriod f3282c;

    public /* synthetic */ i(ProgressiveMediaPeriod progressiveMediaPeriod, int i10) {
        this.f3281b = i10;
        this.f3282c = progressiveMediaPeriod;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3281b) {
            case 0:
                this.f3282c.lambda$onLengthKnown$2();
                break;
            case 1:
                this.f3282c.maybeFinishPrepare();
                break;
            default:
                this.f3282c.lambda$new$0();
                break;
        }
    }
}
