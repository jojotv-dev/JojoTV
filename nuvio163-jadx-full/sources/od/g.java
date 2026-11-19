package od;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class g extends Exception {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final h f43727b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Integer f43728c;

    /* JADX WARN: Illegal instructions before constructor call */
    public g(h failure, Integer num, Throwable th2, int i10) {
        num = (i10 & 2) != 0 ? null : num;
        th2 = (i10 & 4) != 0 ? null : th2;
        Intrinsics.checkNotNullParameter(failure, "failure");
        super(failure.name(), th2);
        this.f43727b = failure;
        this.f43728c = num;
    }
}
