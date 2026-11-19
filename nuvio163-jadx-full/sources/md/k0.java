package md;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class k0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f35215a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Integer f35216b;

    public k0(String name, Integer num) {
        Intrinsics.checkNotNullParameter(name, "name");
        this.f35215a = name;
        this.f35216b = num;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof k0)) {
            return false;
        }
        k0 k0Var = (k0) obj;
        return Intrinsics.areEqual(this.f35215a, k0Var.f35215a) && Intrinsics.areEqual(this.f35216b, k0Var.f35216b);
    }

    public final int hashCode() {
        int iHashCode = this.f35215a.hashCode() * 31;
        Integer num = this.f35216b;
        return iHashCode + (num == null ? 0 : num.hashCode());
    }

    public final String toString() {
        return "TitleName(name=" + this.f35215a + ", year=" + this.f35216b + ")";
    }
}
