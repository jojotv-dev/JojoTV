package m3;

import android.view.Choreographer;
import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class d0 implements Executor {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ Choreographer f34774b;

    @Override // java.util.concurrent.Executor
    public final void execute(Runnable runnable) {
        this.f34774b.postFrameCallback(new e0(runnable));
    }
}
