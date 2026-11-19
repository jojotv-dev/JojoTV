package ld;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class q extends Exception {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final p f32836b;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public q(p error) {
        super(error.name());
        Intrinsics.checkNotNullParameter(error, "error");
        this.f32836b = error;
    }
}
