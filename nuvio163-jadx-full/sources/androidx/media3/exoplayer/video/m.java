package androidx.media3.exoplayer.video;

import androidx.media3.exoplayer.DecoderCounters;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class m implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3390b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ VideoRendererEventListener.EventDispatcher f3391c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ DecoderCounters f3392e;

    public /* synthetic */ m(VideoRendererEventListener.EventDispatcher eventDispatcher, DecoderCounters decoderCounters, int i10) {
        this.f3390b = i10;
        this.f3391c = eventDispatcher;
        this.f3392e = decoderCounters;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3390b) {
            case 0:
                this.f3391c.lambda$enabled$0(this.f3392e);
                break;
            default:
                this.f3391c.lambda$disabled$8(this.f3392e);
                break;
        }
    }
}
