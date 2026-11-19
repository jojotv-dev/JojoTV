package md;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class z extends Exception {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final a0 f35566b;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public z(String str, a0 failure) {
        super(str == null ? failure.name() : str);
        Intrinsics.checkNotNullParameter(failure, "failure");
        this.f35566b = failure;
    }
}
