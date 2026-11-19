package com.fasterxml.jackson.core.util;

import java.util.function.Supplier;
import kotlin.sequences.Sequence;
import kotlin.streams.jdk8.StreamsKt;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class a implements Supplier {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f7504a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ Object f7505b;

    public /* synthetic */ a(Object obj, int i10) {
        this.f7504a = i10;
        this.f7505b = obj;
    }

    @Override // java.util.function.Supplier
    public final Object get() {
        switch (this.f7504a) {
            case 0:
                return ((JsonRecyclerPools.BoundedPool) this.f7505b).lambda$readResolve$0();
            default:
                return StreamsKt.asStream$lambda$0((Sequence) this.f7505b);
        }
    }
}
