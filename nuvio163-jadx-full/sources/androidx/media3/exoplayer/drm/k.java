package androidx.media3.exoplayer.drm;

import com.google.common.util.concurrent.q0;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class k implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3104b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ OfflineLicenseHelper f3105c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ DrmSession f3106e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ q0 f3107i;

    public /* synthetic */ k(DrmSession drmSession, OfflineLicenseHelper offlineLicenseHelper, q0 q0Var) {
        this.f3104b = 1;
        this.f3105c = offlineLicenseHelper;
        this.f3106e = drmSession;
        this.f3107i = q0Var;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3104b) {
            case 0:
                this.f3105c.lambda$getLicenseDurationRemainingSec$0(this.f3107i, this.f3106e);
                break;
            case 1:
                this.f3105c.lambda$acquireFirstSessionOnHandlerThread$3(this.f3106e, this.f3107i);
                break;
            default:
                this.f3105c.lambda$acquireSessionAndGetOfflineLicenseKeySetIdOnHandlerThread$1(this.f3107i, this.f3106e);
                break;
        }
    }

    public /* synthetic */ k(OfflineLicenseHelper offlineLicenseHelper, q0 q0Var, DrmSession drmSession, int i10) {
        this.f3104b = i10;
        this.f3105c = offlineLicenseHelper;
        this.f3107i = q0Var;
        this.f3106e = drmSession;
    }
}
