package re;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class m extends Exception {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final l f53121b;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public m(l error) {
        super(error.name());
        Intrinsics.checkNotNullParameter(error, "error");
        this.f53121b = error;
    }
}
