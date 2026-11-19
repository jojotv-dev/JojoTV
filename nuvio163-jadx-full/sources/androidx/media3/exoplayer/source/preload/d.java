package androidx.media3.exoplayer.source.preload;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class d implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3296b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f3297c;

    public /* synthetic */ d(Object obj, int i10) {
        this.f3296b = i10;
        this.f3297c = obj;
    }

    @Override // java.lang.Runnable
    public final void run() throws Throwable {
        switch (this.f3296b) {
            case 0:
                ((DefaultPreloadManager) this.f3297c).lambda$releaseInternal$1();
                break;
            default:
                ((PreCacheHelper) this.f3297c).lambda$stop$1();
                break;
        }
    }
}
