package k4;

import android.view.View;
import android.view.ViewTreeObserver;

/* JADX INFO: loaded from: classes3.dex */
public final class w implements ViewTreeObserver.OnPreDrawListener, View.OnAttachStateChangeListener {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final View f27854b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public ViewTreeObserver f27855c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final Runnable f27856e;

    public w(View view, Runnable runnable) {
        this.f27854b = view;
        this.f27855c = view.getViewTreeObserver();
        this.f27856e = runnable;
    }

    public static void a(View view, Runnable runnable) {
        if (view == null) {
            throw new NullPointerException("view == null");
        }
        w wVar = new w(view, runnable);
        view.getViewTreeObserver().addOnPreDrawListener(wVar);
        view.addOnAttachStateChangeListener(wVar);
    }

    @Override // android.view.ViewTreeObserver.OnPreDrawListener
    public final boolean onPreDraw() {
        boolean zIsAlive = this.f27855c.isAlive();
        View view = this.f27854b;
        if (zIsAlive) {
            this.f27855c.removeOnPreDrawListener(this);
        } else {
            view.getViewTreeObserver().removeOnPreDrawListener(this);
        }
        view.removeOnAttachStateChangeListener(this);
        this.f27856e.run();
        return true;
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public final void onViewAttachedToWindow(View view) {
        this.f27855c = view.getViewTreeObserver();
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public final void onViewDetachedFromWindow(View view) {
        boolean zIsAlive = this.f27855c.isAlive();
        View view2 = this.f27854b;
        if (zIsAlive) {
            this.f27855c.removeOnPreDrawListener(this);
        } else {
            view2.getViewTreeObserver().removeOnPreDrawListener(this);
        }
        view2.removeOnAttachStateChangeListener(this);
    }
}
