package io.sentry.android.core.internal.util;

import android.os.SystemClock;

/* JADX INFO: loaded from: classes.dex */
public final class d implements io.sentry.transport.f {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final d f24782b = new d();

    @Override // io.sentry.transport.f
    public long c() {
        return SystemClock.uptimeMillis();
    }
}
