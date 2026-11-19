package androidx.media3.exoplayer.rtsp;

import android.os.HandlerThread;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class d implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3242b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f3243c;

    public /* synthetic */ d(Object obj, int i10) {
        this.f3242b = i10;
        this.f3243c = obj;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3242b) {
            case 0:
                RtspMediaPeriod.access$500((RtspMediaPeriod) this.f3243c);
                break;
            case 1:
                RtspMediaPeriod.access$500((RtspMediaPeriod) this.f3243c);
                break;
            default:
                ((HandlerThread) this.f3243c).quit();
                break;
        }
    }
}
