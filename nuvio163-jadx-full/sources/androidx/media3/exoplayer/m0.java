package androidx.media3.exoplayer;

import android.util.Pair;
import androidx.media3.exoplayer.source.MediaLoadData;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class m0 implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3180b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ MediaSourceList.ForwardingEventListener f3181c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Pair f3182e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ Object f3183i;

    public /* synthetic */ m0(MediaSourceList.ForwardingEventListener forwardingEventListener, Pair pair, Object obj, int i10) {
        this.f3180b = i10;
        this.f3181c = forwardingEventListener;
        this.f3182e = pair;
        this.f3183i = obj;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3180b) {
            case 0:
                this.f3181c.lambda$onUpstreamDiscarded$4(this.f3182e, (MediaLoadData) this.f3183i);
                break;
            default:
                this.f3181c.lambda$onDrmSessionManagerError$8(this.f3182e, (Exception) this.f3183i);
                break;
        }
    }
}
