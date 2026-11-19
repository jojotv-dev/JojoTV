package androidx.media3.exoplayer;

import android.util.Pair;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class q0 implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3229b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ MediaSourceList.ForwardingEventListener f3230c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Pair f3231e;

    public /* synthetic */ q0(MediaSourceList.ForwardingEventListener forwardingEventListener, Pair pair, int i10) {
        this.f3229b = i10;
        this.f3230c = forwardingEventListener;
        this.f3231e = pair;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3229b) {
            case 0:
                this.f3230c.lambda$onDrmKeysRemoved$10(this.f3231e);
                break;
            case 1:
                this.f3230c.lambda$onDrmKeysRestored$9(this.f3231e);
                break;
            case 2:
                this.f3230c.lambda$onDrmSessionReleased$11(this.f3231e);
                break;
            default:
                this.f3230c.lambda$onDrmKeysLoaded$7(this.f3231e);
                break;
        }
    }
}
