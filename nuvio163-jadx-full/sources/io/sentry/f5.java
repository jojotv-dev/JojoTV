package io.sentry;

import java.util.concurrent.ThreadFactory;

/* JADX INFO: loaded from: classes.dex */
public final class f5 implements ThreadFactory {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f25546b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f25547c;

    @Override // java.util.concurrent.ThreadFactory
    public final Thread newThread(Runnable runnable) {
        switch (this.f25546b) {
            case 0:
                StringBuilder sb2 = new StringBuilder("SentryExecutorServiceThreadFactory-");
                int i10 = this.f25547c;
                this.f25547c = i10 + 1;
                sb2.append(i10);
                Thread thread = new Thread(runnable, sb2.toString());
                thread.setDaemon(true);
                return thread;
            default:
                StringBuilder sb3 = new StringBuilder("SentryAsyncConnection-");
                int i11 = this.f25547c;
                this.f25547c = i11 + 1;
                sb3.append(i11);
                Thread thread2 = new Thread(runnable, sb3.toString());
                thread2.setDaemon(true);
                return thread2;
        }
    }
}
