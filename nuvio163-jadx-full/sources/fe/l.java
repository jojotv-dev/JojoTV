package fe;

import android.os.Process;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class l implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f17098b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Runnable f17099c;

    public /* synthetic */ l(Runnable runnable, int i10) {
        this.f17098b = i10;
        this.f17099c = runnable;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f17098b) {
            case 0:
                Process.setThreadPriority(9);
                this.f17099c.run();
                break;
            case 1:
                Process.setThreadPriority(10);
                this.f17099c.run();
                break;
            case 2:
                Process.setThreadPriority(10);
                this.f17099c.run();
                break;
            default:
                Process.setThreadPriority(10);
                this.f17099c.run();
                break;
        }
    }
}
