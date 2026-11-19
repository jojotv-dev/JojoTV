package androidx.media3.exoplayer.source;

import androidx.media3.common.util.Consumer;
import androidx.media3.extractor.SeekMap;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class j implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3283b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f3284c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Object f3285e;

    public /* synthetic */ j(Object obj, Object obj2, int i10) {
        this.f3283b = i10;
        this.f3284c = obj;
        this.f3285e = obj2;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3283b) {
            case 0:
                ((ProgressiveMediaPeriod) this.f3284c).lambda$seekMap$1((SeekMap) this.f3285e);
                break;
            default:
                ((Consumer) this.f3284c).accept((MediaSourceEventListener) this.f3285e);
                break;
        }
    }
}
