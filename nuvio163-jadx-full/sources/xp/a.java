package xp;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes3.dex */
public final class a implements Executor {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Handler f64153b = new Handler(Looper.getMainLooper());

    @Override // java.util.concurrent.Executor
    public final void execute(Runnable runnable) {
        this.f64153b.post(runnable);
    }
}
