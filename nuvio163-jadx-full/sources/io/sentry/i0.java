package io.sentry;

import java.util.concurrent.ThreadFactory;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes5.dex */
public final class i0 implements ThreadFactory {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f25608b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f25609c;

    @Override // java.util.concurrent.ThreadFactory
    public final Thread newThread(Runnable r9) {
        switch (this.f25608b) {
            case 0:
                StringBuilder sb2 = new StringBuilder("SentryHostnameCache-");
                int i10 = this.f25609c;
                this.f25609c = i10 + 1;
                sb2.append(i10);
                Thread thread = new Thread(r9, sb2.toString());
                thread.setDaemon(true);
                return thread;
            case 1:
                Intrinsics.checkNotNullParameter(r9, "r");
                StringBuilder sb3 = new StringBuilder("SentryReplayIntegration-");
                int i11 = this.f25609c;
                this.f25609c = i11 + 1;
                sb3.append(i11);
                Thread thread2 = new Thread(r9, sb3.toString());
                thread2.setDaemon(true);
                return thread2;
            default:
                Intrinsics.checkNotNullParameter(r9, "r");
                StringBuilder sb4 = new StringBuilder("SentryReplayPersister-");
                int i12 = this.f25609c;
                this.f25609c = i12 + 1;
                sb4.append(i12);
                Thread thread3 = new Thread(r9, sb4.toString());
                thread3.setDaemon(true);
                return thread3;
        }
    }
}
