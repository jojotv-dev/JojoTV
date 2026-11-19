package androidx.media3.exoplayer.audio;

import androidx.media3.exoplayer.video.VideoRendererEventListener;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class i implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3053b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ String f3054c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ long f3055e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ long f3056i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final /* synthetic */ Object f3057m;

    public /* synthetic */ i(Object obj, String str, long j10, long j11, int i10) {
        this.f3053b = i10;
        this.f3057m = obj;
        this.f3054c = str;
        this.f3055e = j10;
        this.f3056i = j11;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3053b) {
            case 0:
                ((AudioRendererEventListener.EventDispatcher) this.f3057m).lambda$decoderInitialized$1(this.f3054c, this.f3055e, this.f3056i);
                break;
            default:
                ((VideoRendererEventListener.EventDispatcher) this.f3057m).lambda$decoderInitialized$1(this.f3054c, this.f3055e, this.f3056i);
                break;
        }
    }
}
