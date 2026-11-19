package com.google.common.util.concurrent;

import java.util.concurrent.locks.AbstractOwnableSynchronizer;

/* JADX INFO: loaded from: classes4.dex */
public final class h0 extends AbstractOwnableSynchronizer implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final i0 f7544b;

    public h0(i0 i0Var) {
        this.f7544b = i0Var;
    }

    public static void a(h0 h0Var, Thread thread) {
        h0Var.setExclusiveOwnerThread(thread);
    }

    @Override // java.lang.Runnable
    public final void run() {
    }

    public final String toString() {
        return this.f7544b.toString();
    }
}
