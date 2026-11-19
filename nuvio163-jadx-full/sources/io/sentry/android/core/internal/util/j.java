package io.sentry.android.core.internal.util;

import android.app.Activity;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import io.sentry.android.core.m0;
import java.util.concurrent.atomic.AtomicReference;

/* JADX INFO: loaded from: classes.dex */
public final class j implements ViewTreeObserver.OnDrawListener {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Handler f24797b = new Handler(Looper.getMainLooper());

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final AtomicReference f24798c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final Runnable f24799e;

    public j(View view, Runnable runnable) {
        this.f24798c = new AtomicReference(view);
        this.f24799e = runnable;
    }

    public static void a(Activity activity, Runnable runnable, m0 m0Var) {
        Window window = activity.getWindow();
        if (window != null) {
            View viewPeekDecorView = window.peekDecorView();
            if (viewPeekDecorView != null) {
                b(viewPeekDecorView, runnable, m0Var);
            } else {
                Window.Callback callback = window.getCallback();
                window.setCallback(new io.sentry.android.core.performance.i(callback != null ? callback : new io.sentry.android.core.internal.gestures.b(), new androidx.media3.exoplayer.audio.j(1, window, callback, runnable, m0Var)));
            }
        }
    }

    public static void b(View view, Runnable runnable, m0 m0Var) {
        j jVar = new j(view, runnable);
        m0Var.getClass();
        if (Build.VERSION.SDK_INT >= 26 || (view.getViewTreeObserver().isAlive() && view.isAttachedToWindow())) {
            view.getViewTreeObserver().addOnDrawListener(jVar);
        } else {
            view.addOnAttachStateChangeListener(new h(jVar, 0));
        }
    }

    @Override // android.view.ViewTreeObserver.OnDrawListener
    public final void onDraw() {
        View view = (View) this.f24798c.getAndSet(null);
        if (view == null) {
            return;
        }
        view.getViewTreeObserver().addOnGlobalLayoutListener(new i(this, view));
        this.f24797b.postAtFrontOfQueue(this.f24799e);
    }
}
