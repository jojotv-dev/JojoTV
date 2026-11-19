package io.sentry;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/* JADX INFO: loaded from: classes.dex */
public final class z2 implements e1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final z2 f26368a = new z2();

    @Override // io.sentry.e1
    public final void a() {
    }

    @Override // io.sentry.e1
    public final Future b(Runnable runnable, long j10) {
        return new FutureTask(new com.liskovsoft.youtubeapi.service.u(2));
    }

    @Override // io.sentry.e1
    public final void close(long j10) {
    }

    @Override // io.sentry.e1
    public final boolean isClosed() {
        return false;
    }

    @Override // io.sentry.e1
    public final Future submit(Runnable runnable) {
        return new FutureTask(new com.liskovsoft.youtubeapi.service.u(2));
    }
}
