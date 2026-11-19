package mf;

import java.util.concurrent.ThreadFactory;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class x1 implements ThreadFactory {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f38796b;

    @Override // java.util.concurrent.ThreadFactory
    public final Thread newThread(Runnable runnable) {
        switch (this.f38796b) {
            case 0:
                Thread thread = new Thread(runnable, "NuvioFreeboxHttpClient");
                thread.setDaemon(true);
                return thread;
            case 1:
                Thread thread2 = new Thread(runnable, "parallel-ds-worker");
                thread2.setPriority(5);
                thread2.setDaemon(true);
                return thread2;
            case 2:
                return new Thread(new fe.l(runnable, 2), "VodCacheWrite");
            default:
                Thread thread3 = new Thread(runnable, "NuvioSmbHttpClient");
                thread3.setDaemon(true);
                return thread3;
        }
    }
}
