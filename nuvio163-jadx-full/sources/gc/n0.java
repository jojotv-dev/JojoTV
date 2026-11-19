package gc;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class n0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final o0 f18995a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Throwable f18996b;

    public /* synthetic */ n0(o0 o0Var) {
        this(o0Var, null);
    }

    public final Throwable a() {
        return this.f18996b;
    }

    public final o0 b() {
        return this.f18995a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof n0)) {
            return false;
        }
        n0 n0Var = (n0) obj;
        return this.f18995a == n0Var.f18995a && Intrinsics.areEqual(this.f18996b, n0Var.f18996b);
    }

    public final int hashCode() {
        int iHashCode = this.f18995a.hashCode() * 31;
        Throwable th2 = this.f18996b;
        return iHashCode + (th2 == null ? 0 : th2.hashCode());
    }

    public final String toString() {
        return "SessionRefreshOutcome(result=" + this.f18995a + ", error=" + this.f18996b + ")";
    }

    public n0(o0 result, Exception exc) {
        Intrinsics.checkNotNullParameter(result, "result");
        this.f18995a = result;
        this.f18996b = exc;
    }
}
