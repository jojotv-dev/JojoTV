package com.google.common.util.concurrent;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

/* JADX INFO: loaded from: classes.dex */
public abstract class i0 extends AtomicReference implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final ao.e f7545b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final ao.e f7546c;

    static {
        int i10 = 1;
        f7545b = new ao.e(i10);
        f7546c = new ao.e(i10);
    }

    public abstract void a(Throwable th2);

    public abstract void b(Object obj);

    public final void c() {
        ao.e eVar = f7546c;
        ao.e eVar2 = f7545b;
        Runnable runnable = (Runnable) get();
        if (runnable instanceof Thread) {
            h0 h0Var = new h0(this);
            h0.a(h0Var, Thread.currentThread());
            if (compareAndSet(runnable, h0Var)) {
                try {
                    ((Thread) runnable).interrupt();
                } finally {
                    if (((Runnable) getAndSet(eVar2)) == eVar) {
                        LockSupport.unpark((Thread) runnable);
                    }
                }
            }
        }
    }

    public abstract boolean d();

    public abstract Object e();

    public abstract String f();

    public final void g(Thread thread) {
        Runnable runnable = (Runnable) get();
        h0 h0Var = null;
        boolean z2 = false;
        int i10 = 0;
        while (true) {
            boolean z10 = runnable instanceof h0;
            ao.e eVar = f7546c;
            if (!z10 && runnable != eVar) {
                break;
            }
            if (z10) {
                h0Var = (h0) runnable;
            }
            i10++;
            if (i10 <= 1000) {
                Thread.yield();
            } else if (runnable == eVar || compareAndSet(runnable, eVar)) {
                z2 = Thread.interrupted() || z2;
                LockSupport.park(h0Var);
            }
            runnable = (Runnable) get();
        }
        if (z2) {
            thread.interrupt();
        }
    }

    @Override // java.lang.Runnable
    public final void run() {
        Thread threadCurrentThread = Thread.currentThread();
        Object objE = null;
        if (compareAndSet(null, threadCurrentThread)) {
            boolean zD = d();
            ao.e eVar = f7545b;
            if (!zD) {
                try {
                    objE = e();
                } catch (Throwable th2) {
                    try {
                        c6.a.c0(th2);
                        if (!compareAndSet(threadCurrentThread, eVar)) {
                            g(threadCurrentThread);
                        }
                        if (zD) {
                            return;
                        }
                        a(th2);
                        return;
                    } catch (Throwable th3) {
                        if (!compareAndSet(threadCurrentThread, eVar)) {
                            g(threadCurrentThread);
                        }
                        if (!zD) {
                            b(null);
                        }
                        throw th3;
                    }
                }
            }
            if (!compareAndSet(threadCurrentThread, eVar)) {
                g(threadCurrentThread);
            }
            if (zD) {
                return;
            }
            b(objE);
        }
    }

    @Override // java.util.concurrent.atomic.AtomicReference
    public final String toString() {
        String str;
        Runnable runnable = (Runnable) get();
        if (runnable == f7545b) {
            str = "running=[DONE]";
        } else if (runnable instanceof h0) {
            str = "running=[INTERRUPTED]";
        } else if (runnable instanceof Thread) {
            str = "running=[RUNNING ON " + ((Thread) runnable).getName() + "]";
        } else {
            str = "running=[NOT STARTED YET]";
        }
        StringBuilder sbS = a0.i.s(str, ", ");
        sbS.append(f());
        return sbS.toString();
    }
}
