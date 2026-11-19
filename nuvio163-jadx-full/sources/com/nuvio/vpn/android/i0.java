package com.nuvio.vpn.android;

import java.util.function.BooleanSupplier;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class i0 implements BooleanSupplier {
    @Override // java.util.function.BooleanSupplier
    public final boolean getAsBoolean() {
        return Thread.currentThread().isInterrupted();
    }
}
