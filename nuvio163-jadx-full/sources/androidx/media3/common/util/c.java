package androidx.media3.common.util;

import android.os.Handler;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class c implements Executor {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ Handler f2802b;

    @Override // java.util.concurrent.Executor
    public final void execute(Runnable runnable) {
        this.f2802b.post(runnable);
    }
}
