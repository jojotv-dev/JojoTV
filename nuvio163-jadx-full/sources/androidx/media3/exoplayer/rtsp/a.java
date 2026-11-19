package androidx.media3.exoplayer.rtsp;

import java.io.Serializable;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class a implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3236b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f3237c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Serializable f3238e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ Object f3239i;

    public /* synthetic */ a(Object obj, Serializable serializable, Object obj2, int i10) {
        this.f3236b = i10;
        this.f3237c = obj;
        this.f3238e = serializable;
        this.f3239i = obj2;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3236b) {
            case 0:
                ((RtpDataLoadable) this.f3237c).lambda$load$0((String) this.f3238e, (RtpDataChannel) this.f3239i);
                break;
            default:
                ((RtspMessageChannel.Sender) this.f3237c).lambda$send$0((byte[]) this.f3238e, (List) this.f3239i);
                break;
        }
    }
}
