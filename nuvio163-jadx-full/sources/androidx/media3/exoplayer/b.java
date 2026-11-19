package androidx.media3.exoplayer;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class b implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3067b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f3068c;

    public /* synthetic */ b(Object obj, int i10) {
        this.f3067b = i10;
        this.f3068c = obj;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3067b) {
            case 0:
                ((AudioBecomingNoisyManager.AudioBecomingNoisyReceiver) this.f3068c).callListenerIfEnabled();
                break;
            case 1:
                ((DefaultSuitableOutputChecker.ImplApi23) this.f3068c).lambda$disable$2();
                break;
            case 2:
                ((DefaultSuitableOutputChecker.ImplApi35) this.f3068c).lambda$disable$2();
                break;
            case 3:
                ((StreamVolumeManager.VolumeChangeReceiver) this.f3068c).lambda$onReceive$0();
                break;
            default:
                ((MetadataRetriever) this.f3068c).lambda$close$2();
                break;
        }
    }
}
