package androidx.media3.session;

import android.os.Handler;
import android.os.Message;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class y0 implements Handler.Callback {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3736b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f3737c;

    public /* synthetic */ y0(Object obj, int i10) {
        this.f3736b = i10;
        this.f3737c = obj;
    }

    @Override // android.os.Handler.Callback
    public final boolean handleMessage(Message message) {
        switch (this.f3736b) {
            case 0:
                return ((MediaControllerImplBase.FlushCommandQueueHandler) this.f3737c).handleMessage(message);
            default:
                return ((MediaControllerImplLegacy.ControllerCompatCallback) this.f3737c).lambda$new$0(message);
        }
    }
}
