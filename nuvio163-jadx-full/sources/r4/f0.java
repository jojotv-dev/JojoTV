package r4;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class f0 extends l0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Throwable f51077b;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public f0(Throwable readException, int i10) {
        super(i10);
        Intrinsics.checkNotNullParameter(readException, "readException");
        this.f51077b = readException;
    }
}
