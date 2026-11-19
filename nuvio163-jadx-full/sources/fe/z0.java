package fe;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class z0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f17898a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Integer f17899b;

    public z0(String type, Integer num) {
        Intrinsics.checkNotNullParameter(type, "type");
        this.f17898a = type;
        this.f17899b = num;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof z0)) {
            return false;
        }
        z0 z0Var = (z0) obj;
        return Intrinsics.areEqual(this.f17898a, z0Var.f17898a) && Intrinsics.areEqual(this.f17899b, z0Var.f17899b);
    }

    public final int hashCode() {
        int iHashCode = this.f17898a.hashCode() * 31;
        Integer num = this.f17899b;
        return iHashCode + (num == null ? 0 : num.hashCode());
    }

    public final String toString() {
        return "TmdbFavourite(type=" + this.f17898a + ", year=" + this.f17899b + ")";
    }
}
