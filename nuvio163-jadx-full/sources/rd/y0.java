package rd;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class y0 extends Exception {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final x0 f52726b;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public y0(x0 error, Throwable th2) {
        super(error.name(), th2);
        Intrinsics.checkNotNullParameter(error, "error");
        this.f52726b = error;
    }
}
