package androidx.core.app;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.SparseIntArray;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/* JADX INFO: loaded from: classes3.dex */
public final class k {

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static HandlerThread f2286f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static Handler f2287g;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f2288a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f2289b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public Serializable f2290c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final Serializable f2291d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final Object f2292e;

    public k() {
        this.f2288a = 1;
        this.f2290c = new ReentrantLock();
        this.f2291d = new ConcurrentHashMap();
        this.f2292e = new LinkedList();
        this.f2289b = 400;
    }

    public static void a(SparseIntArray sparseIntArray, long j10) {
        if (sparseIntArray != null) {
            int i10 = (int) ((500000 + j10) / 1000000);
            if (j10 >= 0) {
                sparseIntArray.put(i10, sparseIntArray.get(i10) + 1);
            }
        }
    }

    public void b(String str) {
        LinkedList linkedList = (LinkedList) this.f2292e;
        ReentrantLock reentrantLock = (ReentrantLock) this.f2290c;
        reentrantLock.lock();
        try {
            linkedList.removeFirstOccurrence(str);
            linkedList.addFirst(str);
        } finally {
            reentrantLock.unlock();
        }
    }

    public String toString() {
        switch (this.f2288a) {
            case 1:
                return ((ConcurrentHashMap) this.f2291d).toString();
            default:
                return super.toString();
        }
    }

    /* JADX WARN: Type inference failed for: r0v2, types: [android.util.SparseIntArray[], java.io.Serializable] */
    public k(int i10) {
        this.f2288a = 0;
        this.f2290c = new SparseIntArray[9];
        this.f2291d = new ArrayList();
        this.f2292e = new j(this);
        this.f2289b = i10;
    }
}
