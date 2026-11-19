package org.chromium.base;

import J.N;
import org.chromium.base.annotations.CalledByNative;

/* JADX INFO: loaded from: classes5.dex */
public class JavaExceptionReporter implements Thread.UncaughtExceptionHandler {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Thread.UncaughtExceptionHandler f44114b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final boolean f44115c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public boolean f44116e;

    public JavaExceptionReporter(Thread.UncaughtExceptionHandler uncaughtExceptionHandler, boolean z2) {
        this.f44114b = uncaughtExceptionHandler;
        this.f44115c = z2;
    }

    @CalledByNative
    public static void installHandler(boolean z2) {
        Thread.setDefaultUncaughtExceptionHandler(new JavaExceptionReporter(Thread.getDefaultUncaughtExceptionHandler(), z2));
    }

    @Override // java.lang.Thread.UncaughtExceptionHandler
    public final void uncaughtException(Thread thread, Throwable th2) {
        if (!this.f44116e) {
            this.f44116e = true;
            N.MLlibBXh(this.f44115c, th2);
        }
        Thread.UncaughtExceptionHandler uncaughtExceptionHandler = this.f44114b;
        if (uncaughtExceptionHandler != null) {
            uncaughtExceptionHandler.uncaughtException(thread, th2);
        }
    }
}
