package ld;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class w4 implements y4 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final x f33160a;

    public w4(x scope) {
        Intrinsics.checkNotNullParameter(scope, "scope");
        this.f33160a = scope;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof w4) && Intrinsics.areEqual(this.f33160a, ((w4) obj).f33160a);
    }

    public final int hashCode() {
        return this.f33160a.hashCode();
    }

    public final String toString() {
        return "Account(scope=" + this.f33160a + ")";
    }
}
