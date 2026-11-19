package io.sentry.android.replay.util;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes5.dex */
public final class g implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f25318b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Runnable f25319c;

    public g(String taskName, Runnable delegate) {
        Intrinsics.checkNotNullParameter(taskName, "taskName");
        Intrinsics.checkNotNullParameter(delegate, "delegate");
        this.f25318b = taskName;
        this.f25319c = delegate;
    }

    @Override // java.lang.Runnable
    public final void run() {
        this.f25319c.run();
    }
}
