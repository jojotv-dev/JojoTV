package androidx.media3.exoplayer.mediacodec;

import android.media.MediaCodec;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class a implements MediaCodec.OnFrameRenderedListener {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f3184a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ MediaCodecAdapter.OnFrameRenderedListener f3185b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ MediaCodecAdapter f3186c;

    public /* synthetic */ a(MediaCodecAdapter mediaCodecAdapter, MediaCodecAdapter.OnFrameRenderedListener onFrameRenderedListener, int i10) {
        this.f3184a = i10;
        this.f3186c = mediaCodecAdapter;
        this.f3185b = onFrameRenderedListener;
    }

    @Override // android.media.MediaCodec.OnFrameRenderedListener
    public final void onFrameRendered(MediaCodec mediaCodec, long j10, long j11) {
        switch (this.f3184a) {
            case 0:
                ((AsynchronousMediaCodecAdapter) this.f3186c).lambda$setOnFrameRenderedListener$0(this.f3185b, mediaCodec, j10, j11);
                break;
            default:
                ((SynchronousMediaCodecAdapter) this.f3186c).lambda$setOnFrameRenderedListener$0(this.f3185b, mediaCodec, j10, j11);
                break;
        }
    }
}
