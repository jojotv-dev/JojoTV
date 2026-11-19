package com.fasterxml.jackson.core.util;

import java.util.function.Supplier;
import org.jsoup.nodes.Entities;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class b implements Supplier {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f7506a;

    public /* synthetic */ b(int i10) {
        this.f7506a = i10;
    }

    @Override // java.util.function.Supplier
    public final Object get() {
        switch (this.f7506a) {
            case 0:
                return JsonRecyclerPools.ConcurrentDequePool.construct();
            case 1:
                return JsonRecyclerPools.LockFreePool.construct();
            case 2:
                return new ig.b(null);
            case 3:
                return new ig.b(null);
            default:
                return Entities.lambda$static$0();
        }
    }
}
