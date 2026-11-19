package gc;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class c0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final e0 f18870a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Throwable f18871b;

    public c0(e0 result, Throwable th2) {
        Intrinsics.checkNotNullParameter(result, "result");
        this.f18870a = result;
        this.f18871b = th2;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof c0)) {
            return false;
        }
        c0 c0Var = (c0) obj;
        return this.f18870a == c0Var.f18870a && Intrinsics.areEqual(this.f18871b, c0Var.f18871b);
    }

    public final int hashCode() {
        int iHashCode = this.f18870a.hashCode() * 31;
        Throwable th2 = this.f18871b;
        return iHashCode + (th2 == null ? 0 : th2.hashCode());
    }

    public final String toString() {
        return "AuthSessionValidationOutcome(result=" + this.f18870a + ", error=" + this.f18871b + ")";
    }
}
