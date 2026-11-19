package org.chromium.base;

import org.chromium.base.annotations.CalledByNative;
import vn.c;

/* JADX INFO: loaded from: classes5.dex */
public class EarlyTraceEvent {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final Object f44112a = new Object();

    @CalledByNative
    public static boolean getBackgroundStartupTracingFlag() {
        return false;
    }

    @CalledByNative
    public static void setBackgroundStartupTracingFlag(boolean z2) {
        c.f61118a.edit().putBoolean("bg_startup_tracing", z2).apply();
    }
}
