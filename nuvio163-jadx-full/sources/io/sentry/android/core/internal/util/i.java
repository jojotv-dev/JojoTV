package io.sentry.android.core.internal.util;

import android.view.View;
import android.view.ViewTreeObserver;

/* JADX INFO: loaded from: classes.dex */
public final class i implements ViewTreeObserver.OnGlobalLayoutListener {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ View f24795b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ j f24796c;

    public i(j jVar, View view) {
        this.f24796c = jVar;
        this.f24795b = view;
    }

    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
    public final void onGlobalLayout() {
        View view = this.f24795b;
        view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        view.getViewTreeObserver().removeOnDrawListener(this.f24796c);
    }
}
