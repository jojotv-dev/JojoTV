package org.chromium.base;

import android.os.Handler;
import android.os.HandlerThread;
import org.chromium.base.annotations.CalledByNative;
import vn.d;
import vn.e;
import vn.f;

/* JADX INFO: loaded from: classes5.dex */
public class JavaHandlerThread {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final HandlerThread f44117a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public Throwable f44118b;

    public JavaHandlerThread(String str, int i10) {
        this.f44117a = new HandlerThread(str, i10);
    }

    @CalledByNative
    public static JavaHandlerThread create(String str, int i10) {
        return new JavaHandlerThread(str, i10);
    }

    @CalledByNative
    public final Throwable getUncaughtExceptionIfAny() {
        return this.f44118b;
    }

    @CalledByNative
    public final boolean isAlive() {
        return this.f44117a.isAlive();
    }

    @CalledByNative
    public final void joinThread() {
        boolean z2 = false;
        while (!z2) {
            try {
                this.f44117a.join();
                z2 = true;
            } catch (InterruptedException unused) {
            }
        }
    }

    @CalledByNative
    public final void listenForUncaughtExceptionsForTesting() {
        this.f44117a.setUncaughtExceptionHandler(new f(this));
    }

    @CalledByNative
    public final void quitThreadSafely(long j10) {
        HandlerThread handlerThread = this.f44117a;
        new Handler(handlerThread.getLooper()).post(new e(this, j10, 0));
        handlerThread.getLooper().quitSafely();
    }

    @CalledByNative
    public final void startAndInitialize(long j10, long j11) {
        HandlerThread handlerThread = this.f44117a;
        if (handlerThread.getState() == Thread.State.NEW) {
            handlerThread.start();
        }
        new Handler(handlerThread.getLooper()).post(new d(j10, j11));
    }
}
