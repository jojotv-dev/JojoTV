package io.sentry.android.core.internal.util;

import android.view.View;
import android.view.ViewTreeObserver;
import n.c0;

/* JADX INFO: loaded from: classes5.dex */
public final class h implements View.OnAttachStateChangeListener {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f24793b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f24794c;

    public /* synthetic */ h(Object obj, int i10) {
        this.f24793b = i10;
        this.f24794c = obj;
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public final void onViewAttachedToWindow(View view) {
        switch (this.f24793b) {
            case 0:
                view.getViewTreeObserver().addOnDrawListener((j) this.f24794c);
                view.removeOnAttachStateChangeListener(this);
                break;
        }
    }

    @Override // android.view.View.OnAttachStateChangeListener
    public final void onViewDetachedFromWindow(View view) {
        switch (this.f24793b) {
            case 0:
                view.removeOnAttachStateChangeListener(this);
                break;
            case 1:
                n.f fVar = (n.f) this.f24794c;
                ViewTreeObserver viewTreeObserver = fVar.F;
                if (viewTreeObserver != null) {
                    if (!viewTreeObserver.isAlive()) {
                        fVar.F = view.getViewTreeObserver();
                    }
                    fVar.F.removeGlobalOnLayoutListener(fVar.f39310q);
                }
                view.removeOnAttachStateChangeListener(this);
                break;
            default:
                c0 c0Var = (c0) this.f24794c;
                ViewTreeObserver viewTreeObserver2 = c0Var.f39293w;
                if (viewTreeObserver2 != null) {
                    if (!viewTreeObserver2.isAlive()) {
                        c0Var.f39293w = view.getViewTreeObserver();
                    }
                    c0Var.f39293w.removeGlobalOnLayoutListener(c0Var.f39287q);
                }
                view.removeOnAttachStateChangeListener(this);
                break;
        }
    }

    private final void a(View view) {
    }

    private final void b(View view) {
    }
}
