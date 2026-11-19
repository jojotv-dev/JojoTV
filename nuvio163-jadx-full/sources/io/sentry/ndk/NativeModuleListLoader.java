package io.sentry.ndk;

/* JADX INFO: loaded from: classes.dex */
public final class NativeModuleListLoader {
    public static native void nativeClearModuleList();

    public static native DebugImage[] nativeLoadModuleList();
}
