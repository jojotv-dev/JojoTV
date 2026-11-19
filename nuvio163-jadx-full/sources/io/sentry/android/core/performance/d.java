package io.sentry.android.core.performance;

import android.os.MessageQueue;
import android.os.SystemClock;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class d implements MessageQueue.IdleHandler {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ g f24908a;

    @Override // android.os.MessageQueue.IdleHandler
    public final boolean queueIdle() {
        g gVar = this.f24908a;
        gVar.f24913e = SystemClock.uptimeMillis();
        gVar.f24926x.set(false);
        gVar.d();
        return false;
    }
}
