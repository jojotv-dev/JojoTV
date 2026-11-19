package io.sentry.android.core.internal.util;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import io.sentry.d5;

/* JADX INFO: loaded from: classes.dex */
public final class e implements io.sentry.util.thread.a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final e f24783a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static volatile long f24784b;

    static {
        e eVar = new e();
        new Handler(Looper.getMainLooper()).post(new d5(1));
        f24783a = eVar;
        f24784b = Process.myTid();
    }

    public static long d(Thread thread) {
        return Build.VERSION.SDK_INT >= 36 ? thread.threadId() : thread.getId();
    }

    @Override // io.sentry.util.thread.a
    public final boolean a() {
        return d(Looper.getMainLooper().getThread()) == d(Thread.currentThread());
    }

    @Override // io.sentry.util.thread.a
    public final String b() {
        return a() ? "main" : Thread.currentThread().getName();
    }

    @Override // io.sentry.util.thread.a
    public final long c() {
        return Process.myTid();
    }
}
