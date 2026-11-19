package androidx.media3.common.util;

import android.content.Context;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class e implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f2804b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f2805c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Object f2806e;

    public /* synthetic */ e(Object obj, Object obj2, int i10) {
        this.f2804b = i10;
        this.f2805c = obj;
        this.f2806e = obj2;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f2804b) {
            case 0:
                ((NetworkTypeObserver.Receiver) this.f2805c).lambda$onReceive$0((Context) this.f2806e);
                break;
            case 1:
                ((BackgroundThreadStateHandler) this.f2805c).lambda$setStateInBackground$2(this.f2806e);
                break;
            default:
                ((NetworkTypeObserver) this.f2805c).lambda$new$0((Context) this.f2806e);
                break;
        }
    }
}
