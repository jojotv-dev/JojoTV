package io.sentry.util;

import io.sentry.f1;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.ReentrantLock;

/* JADX INFO: loaded from: classes.dex */
public final class a implements f1 {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final AtomicReferenceFieldUpdater f26249c = AtomicReferenceFieldUpdater.newUpdater(a.class, ReentrantLock.class, "b");

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public volatile ReentrantLock f26250b;

    @Override // java.lang.AutoCloseable
    public final void close() {
        ReentrantLock reentrantLock = this.f26250b;
        b.p(reentrantLock, "close() called before acquire()");
        reentrantLock.unlock();
    }

    public final void g() {
        ReentrantLock reentrantLock = this.f26250b;
        if (reentrantLock == null) {
            reentrantLock = new ReentrantLock();
            AtomicReferenceFieldUpdater atomicReferenceFieldUpdater = f26249c;
            while (!atomicReferenceFieldUpdater.compareAndSet(this, null, reentrantLock)) {
                if (atomicReferenceFieldUpdater.get(this) != null) {
                    reentrantLock = this.f26250b;
                    b.p(reentrantLock, "lock must have been set by the winning thread");
                    break;
                }
            }
        }
        reentrantLock.lock();
    }
}
