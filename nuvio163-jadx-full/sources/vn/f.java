package vn;

import org.chromium.base.JavaHandlerThread;

/* JADX INFO: loaded from: classes5.dex */
public final class f implements Thread.UncaughtExceptionHandler {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ JavaHandlerThread f61124b;

    public f(JavaHandlerThread javaHandlerThread) {
        this.f61124b = javaHandlerThread;
    }

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public final void uncaughtException(Thread thread, Throwable th2) {
        this.f61124b.f44118b = th2;
    }
}
