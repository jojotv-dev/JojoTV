package io.sentry;

import java.util.concurrent.Future;

/* JADX INFO: loaded from: classes.dex */
public interface e1 {
    void a();

    Future b(Runnable runnable, long j10);

    void close(long j10);

    boolean isClosed();

    Future submit(Runnable runnable);
}
