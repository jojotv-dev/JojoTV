package g4;

import android.os.Process;

/* JADX INFO: loaded from: classes3.dex */
public final class j extends Thread {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f18801b;

    public j(Runnable runnable) {
        super(runnable, "fonts-androidx");
        this.f18801b = 10;
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public final void run() {
        Process.setThreadPriority(this.f18801b);
        super.run();
    }
}
