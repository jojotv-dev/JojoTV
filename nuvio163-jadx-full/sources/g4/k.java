package g4;

import java.util.concurrent.ThreadFactory;

/* JADX INFO: loaded from: classes3.dex */
public final class k implements ThreadFactory {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f18802b;

    @Override // java.util.concurrent.ThreadFactory
    public final Thread newThread(Runnable runnable) {
        switch (this.f18802b) {
            case 0:
                return new j(runnable);
            default:
                Thread thread = new Thread(runnable, "smbj-lease-break");
                thread.setDaemon(true);
                return thread;
        }
    }
}
