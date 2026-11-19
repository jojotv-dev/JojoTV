package mf;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class jl {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final kl f36769a;

    public jl(kl route) {
        Intrinsics.checkNotNullParameter(route, "route");
        this.f36769a = route;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof jl) && Intrinsics.areEqual(this.f36769a, ((jl) obj).f36769a);
    }

    public final int hashCode() {
        return this.f36769a.hashCode();
    }

    public final String toString() {
        return "Routed(route=" + this.f36769a + ")";
    }
}
