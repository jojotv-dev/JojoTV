package androidx.media3.exoplayer;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class a implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f2921b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f2922c;

    public /* synthetic */ a(Object obj, int i10) {
        this.f2921b = i10;
        this.f2922c = obj;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f2921b) {
            case 0:
                ((AudioBecomingNoisyManager) this.f2922c).lambda$setEnabled$0();
                break;
            case 1:
                ((AudioBecomingNoisyManager) this.f2922c).lambda$setEnabled$1();
                break;
            default:
                ((ExoPlayerImpl) this.f2922c).lambda$new$3();
                break;
        }
    }
}
