package com.google.common.util.concurrent;

import java.util.concurrent.Callable;

/* JADX INFO: loaded from: classes4.dex */
public final class b0 implements Callable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ androidx.media3.exoplayer.b f7522b;

    public b0(androidx.media3.exoplayer.b bVar) {
        this.f7522b = bVar;
    }

    @Override // java.util.concurrent.Callable
    public final Object call() {
        this.f7522b.run();
        return null;
    }
}
