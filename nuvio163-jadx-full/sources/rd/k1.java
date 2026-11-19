package rd;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class k1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final i1 f52243a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f52244b;

    public k1(String accessToken, i1 scope) {
        Intrinsics.checkNotNullParameter(scope, "scope");
        Intrinsics.checkNotNullParameter(accessToken, "accessToken");
        this.f52243a = scope;
        this.f52244b = accessToken;
    }

    public final String a() {
        return this.f52244b;
    }

    public final i1 b() {
        return this.f52243a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof k1)) {
            return false;
        }
        k1 k1Var = (k1) obj;
        return Intrinsics.areEqual(this.f52243a, k1Var.f52243a) && Intrinsics.areEqual(this.f52244b, k1Var.f52244b);
    }

    public final int hashCode() {
        return this.f52244b.hashCode() + (this.f52243a.hashCode() * 31);
    }

    public final String toString() {
        return "SimklAuthorization(scope=" + this.f52243a + ", accessToken=" + this.f52244b + ")";
    }
}
