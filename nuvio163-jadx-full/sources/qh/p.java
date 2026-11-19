package qh;

import java.util.concurrent.ThreadFactory;

/* JADX INFO: loaded from: classes5.dex */
public final class p extends eh.k {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final s f50776d = new s("RxNewThreadScheduler", Math.max(1, Math.min(10, Integer.getInteger("rx2.newthread-priority", 5).intValue())), false);

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final ThreadFactory f50777c = f50776d;

    @Override // eh.k
    public final eh.j a() {
        return new q(this.f50777c);
    }
}
