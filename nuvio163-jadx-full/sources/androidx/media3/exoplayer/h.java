package androidx.media3.exoplayer;

import android.content.Context;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class h implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3123b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f3124c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Object f3125e;

    public /* synthetic */ h(Object obj, Object obj2, int i10) {
        this.f3123b = i10;
        this.f3125e = obj;
        this.f3124c = obj2;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3123b) {
            case 0:
                ((DefaultSuitableOutputChecker.ImplApi23) this.f3125e).lambda$enable$1((Context) this.f3124c);
                break;
            case 1:
                ((DefaultSuitableOutputChecker.ImplApi35) this.f3125e).lambda$enable$1((Context) this.f3124c);
                break;
            case 2:
                ((ExoPlayerImpl) this.f3125e).lambda$new$1((ExoPlayerImplInternal.PlaybackInfoUpdate) this.f3124c);
                break;
            default:
                ((ExoPlayerImplInternal) this.f3125e).lambda$sendMessageToTargetThread$2((PlayerMessage) this.f3124c);
                break;
        }
    }
}
