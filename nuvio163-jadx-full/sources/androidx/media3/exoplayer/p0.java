package androidx.media3.exoplayer;

import android.util.Pair;
import androidx.media3.exoplayer.source.LoadEventInfo;
import androidx.media3.exoplayer.source.MediaLoadData;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class p0 implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3224b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ MediaSourceList.ForwardingEventListener f3225c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Pair f3226e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ LoadEventInfo f3227i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final /* synthetic */ MediaLoadData f3228m;

    public /* synthetic */ p0(MediaSourceList.ForwardingEventListener forwardingEventListener, Pair pair, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, int i10) {
        this.f3224b = i10;
        this.f3225c = forwardingEventListener;
        this.f3226e = pair;
        this.f3227i = loadEventInfo;
        this.f3228m = mediaLoadData;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3224b) {
            case 0:
                this.f3225c.lambda$onLoadCanceled$2(this.f3226e, this.f3227i, this.f3228m);
                break;
            default:
                this.f3225c.lambda$onLoadCompleted$1(this.f3226e, this.f3227i, this.f3228m);
                break;
        }
    }
}
