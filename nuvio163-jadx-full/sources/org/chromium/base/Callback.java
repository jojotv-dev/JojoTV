package org.chromium.base;

import org.chromium.base.annotations.CalledByNative;

/* JADX INFO: loaded from: classes5.dex */
public interface Callback {

    public static abstract class Helper {
        @CalledByNative
        public static void onBooleanResultFromNative(Callback callback, boolean z2) {
            callback.a();
        }

        @CalledByNative
        public static void onIntResultFromNative(Callback callback, int i10) {
            callback.a();
        }

        @CalledByNative
        public static void onLongResultFromNative(Callback callback, long j10) {
            callback.a();
        }

        @CalledByNative
        public static void onObjectResultFromNative(Callback callback, Object obj) {
            callback.a();
        }

        @CalledByNative
        public static void onTimeResultFromNative(Callback callback, long j10) {
            callback.a();
        }

        @CalledByNative
        public static void runRunnable(Runnable runnable) {
            runnable.run();
        }
    }

    void a();
}
