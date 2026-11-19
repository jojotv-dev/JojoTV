package r4;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class c0 extends l0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Throwable f51066b;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public c0(Throwable finalException) {
        super(Integer.MAX_VALUE);
        Intrinsics.checkNotNullParameter(finalException, "finalException");
        this.f51066b = finalException;
    }
}
