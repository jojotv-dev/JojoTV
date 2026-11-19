package androidx.media3.common.util;

import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes.dex */
@UnstableApi
public final class BackgroundExecutor {
    private static Executor staticInstance;

    private BackgroundExecutor() {
    }

    public static synchronized Executor get() {
        try {
            if (staticInstance == null) {
                staticInstance = Util.newSingleThreadExecutor("ExoPlayer:BackgroundExecutor");
            }
        } catch (Throwable th2) {
            throw th2;
        }
        return staticInstance;
    }

    public static synchronized void set(Executor executor) {
        staticInstance = executor;
    }
}
