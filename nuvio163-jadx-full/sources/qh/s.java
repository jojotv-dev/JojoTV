package qh;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/* JADX INFO: loaded from: classes5.dex */
public final class s extends AtomicLong implements ThreadFactory {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f50780b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f50781c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final boolean f50782e;

    public s(String str) {
        this(str, 5, false);
    }

    @Override // java.util.concurrent.ThreadFactory
    public final Thread newThread(Runnable runnable) {
        String str = this.f50780b + '-' + incrementAndGet();
        Thread rVar = this.f50782e ? new r(runnable, str) : new Thread(runnable, str);
        rVar.setPriority(this.f50781c);
        rVar.setDaemon(true);
        return rVar;
    }

    @Override // java.util.concurrent.atomic.AtomicLong
    public final String toString() {
        return o2.a.q(new StringBuilder("RxThreadFactory["), this.f50780b, "]");
    }

    public s(String str, int i10, boolean z2) {
        this.f50780b = str;
        this.f50781c = i10;
        this.f50782e = z2;
    }
}
