package com.google.common.util.concurrent;

import java.util.concurrent.Executor;

/* JADX INFO: loaded from: classes.dex */
public final class e {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final e f7529d = new e();

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Runnable f7530a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Executor f7531b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public e f7532c;

    public e(Runnable runnable, Executor executor) {
        this.f7530a = runnable;
        this.f7531b = executor;
    }

    public e() {
        this.f7530a = null;
        this.f7531b = null;
    }
}
