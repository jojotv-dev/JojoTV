package h3;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class q0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final j0 f20731a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final j0 f20732b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final j0 f20733c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final j0 f20734d;

    public q0(j0 j0Var, j0 j0Var2, j0 j0Var3, j0 j0Var4) {
        this.f20731a = j0Var;
        this.f20732b = j0Var2;
        this.f20733c = j0Var3;
        this.f20734d = j0Var4;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof q0)) {
            return false;
        }
        q0 q0Var = (q0) obj;
        return Intrinsics.areEqual(this.f20731a, q0Var.f20731a) && Intrinsics.areEqual(this.f20732b, q0Var.f20732b) && Intrinsics.areEqual(this.f20733c, q0Var.f20733c) && Intrinsics.areEqual(this.f20734d, q0Var.f20734d);
    }

    public final int hashCode() {
        j0 j0Var = this.f20731a;
        int iHashCode = (j0Var != null ? j0Var.hashCode() : 0) * 31;
        j0 j0Var2 = this.f20732b;
        int iHashCode2 = (iHashCode + (j0Var2 != null ? j0Var2.hashCode() : 0)) * 31;
        j0 j0Var3 = this.f20733c;
        int iHashCode3 = (iHashCode2 + (j0Var3 != null ? j0Var3.hashCode() : 0)) * 31;
        j0 j0Var4 = this.f20734d;
        return iHashCode3 + (j0Var4 != null ? j0Var4.hashCode() : 0);
    }
}
