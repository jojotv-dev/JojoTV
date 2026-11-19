package androidx.media3.common.util;

import android.os.Looper;
import androidx.media3.common.m;

/* JADX INFO: loaded from: classes.dex */
@UnstableApi
public final class BackgroundThreadStateHandler<T> {
    private final HandlerWrapper backgroundHandler;
    private T backgroundState;
    private final HandlerWrapper foregroundHandler;
    private T foregroundState;
    private final StateChangeListener<T> onStateChanged;
    private int pendingOperations;

    /* JADX INFO: loaded from: classes3.dex */
    public interface StateChangeListener<T> {
        void onStateChanged(T t10, T t11);
    }

    public BackgroundThreadStateHandler(T t10, Looper looper, Looper looper2, Clock clock, StateChangeListener<T> stateChangeListener) {
        this.backgroundHandler = clock.createHandler(looper, null);
        this.foregroundHandler = clock.createHandler(looper2, null);
        this.foregroundState = t10;
        this.backgroundState = t10;
        this.onStateChanged = stateChangeListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public /* synthetic */ void lambda$setStateInBackground$2(Object obj) {
        if (this.pendingOperations == 0) {
            updateStateInForeground(obj);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public /* synthetic */ void lambda$updateStateAsync$0(Object obj) {
        int i10 = this.pendingOperations - 1;
        this.pendingOperations = i10;
        if (i10 == 0) {
            updateStateInForeground(obj);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateStateAsync$1(q7.f fVar) {
        T t10 = (T) fVar.apply(this.backgroundState);
        this.backgroundState = t10;
        runInForeground(new m(this, t10, 2));
    }

    private void runInForeground(Runnable runnable) {
        if (this.foregroundHandler.getLooper().getThread().isAlive()) {
            this.foregroundHandler.post(runnable);
        }
    }

    private void updateStateInForeground(T t10) {
        T t11 = this.foregroundState;
        this.foregroundState = t10;
        if (t11.equals(t10)) {
            return;
        }
        this.onStateChanged.onStateChanged(t11, t10);
    }

    public T get() {
        Looper looperMyLooper = Looper.myLooper();
        if (looperMyLooper == this.foregroundHandler.getLooper()) {
            return this.foregroundState;
        }
        Assertions.checkState(looperMyLooper == this.backgroundHandler.getLooper());
        return this.backgroundState;
    }

    public void runInBackground(Runnable runnable) {
        if (this.backgroundHandler.getLooper().getThread().isAlive()) {
            this.backgroundHandler.post(runnable);
        }
    }

    public void setStateInBackground(T t10) {
        this.backgroundState = t10;
        runInForeground(new e(this, t10, 1));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void updateStateAsync(q7.f fVar, q7.f fVar2) {
        Assertions.checkState(Looper.myLooper() == this.foregroundHandler.getLooper());
        this.pendingOperations++;
        runInBackground(new m(this, fVar2, 1));
        updateStateInForeground(fVar.apply(this.foregroundState));
    }
}
