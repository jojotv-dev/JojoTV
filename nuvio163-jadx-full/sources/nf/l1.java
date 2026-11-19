package nf;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class l1 extends n1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f42349a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final c1 f42350b;

    public l1(String url, c1 c1Var) {
        Intrinsics.checkNotNullParameter(url, "url");
        this.f42349a = url;
        this.f42350b = c1Var;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof l1)) {
            return false;
        }
        l1 l1Var = (l1) obj;
        return Intrinsics.areEqual(this.f42349a, l1Var.f42349a) && Intrinsics.areEqual(this.f42350b, l1Var.f42350b);
    }

    public final int hashCode() {
        int iHashCode = this.f42349a.hashCode() * 31;
        c1 c1Var = this.f42350b;
        return iHashCode + (c1Var == null ? 0 : c1Var.hashCode());
    }

    public final String toString() {
        return "CandidateLoaded(url=" + this.f42349a + ", loaded=" + this.f42350b + ")";
    }
}
