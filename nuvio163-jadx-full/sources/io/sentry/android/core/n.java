package io.sentry.android.core;

import android.os.Debug;
import io.sentry.k3;

/* JADX INFO: loaded from: classes.dex */
public final class n implements io.sentry.w0 {
    @Override // io.sentry.w0
    public final void a() {
    }

    @Override // io.sentry.w0
    public final void b(k3 k3Var) {
        long jFreeMemory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        long nativeHeapSize = Debug.getNativeHeapSize() - Debug.getNativeHeapFreeSize();
        k3Var.f(Long.valueOf(jFreeMemory));
        k3Var.g(Long.valueOf(nativeHeapSize));
    }
}
