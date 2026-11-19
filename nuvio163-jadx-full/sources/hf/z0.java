package hf;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class z0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Integer f23383a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Integer f23384b;

    public z0(Integer num, Integer num2) {
        this.f23383a = num;
        this.f23384b = num2;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof z0)) {
            return false;
        }
        z0 z0Var = (z0) obj;
        return Intrinsics.areEqual(this.f23383a, z0Var.f23383a) && Intrinsics.areEqual(this.f23384b, z0Var.f23384b);
    }

    public final int hashCode() {
        Integer num = this.f23383a;
        int iHashCode = (num == null ? 0 : num.hashCode()) * 31;
        Integer num2 = this.f23384b;
        return iHashCode + (num2 != null ? num2.hashCode() : 0);
    }

    public final String toString() {
        return "DetailReturnEpisodeFocusRequest(season=" + this.f23383a + ", episode=" + this.f23384b + ")";
    }
}
