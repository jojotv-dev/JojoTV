package androidx.media3.exoplayer.audio;

import androidx.media3.exoplayer.DecoderCounters;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class a implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3028b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ AudioRendererEventListener.EventDispatcher f3029c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ DecoderCounters f3030e;

    public /* synthetic */ a(AudioRendererEventListener.EventDispatcher eventDispatcher, DecoderCounters decoderCounters, int i10) {
        this.f3028b = i10;
        this.f3029c = eventDispatcher;
        this.f3030e = decoderCounters;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3028b) {
            case 0:
                this.f3029c.lambda$enabled$0(this.f3030e);
                break;
            default:
                this.f3029c.lambda$disabled$6(this.f3030e);
                break;
        }
    }
}
