package ne;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class d0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Integer f40184a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f40185b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Double f40186c;

    public d0(Integer num, String str, Double d10) {
        this.f40184a = num;
        this.f40185b = str;
        this.f40186c = d10;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof d0)) {
            return false;
        }
        d0 d0Var = (d0) obj;
        return Intrinsics.areEqual(this.f40184a, d0Var.f40184a) && Intrinsics.areEqual(this.f40185b, d0Var.f40185b) && Intrinsics.areEqual((Object) this.f40186c, (Object) d0Var.f40186c);
    }

    public final int hashCode() {
        Integer num = this.f40184a;
        int iHashCode = (num == null ? 0 : num.hashCode()) * 31;
        String str = this.f40185b;
        int iHashCode2 = (iHashCode + (str == null ? 0 : str.hashCode())) * 31;
        Double d10 = this.f40186c;
        return iHashCode2 + (d10 != null ? d10.hashCode() : 0);
    }

    public final String toString() {
        return "PreGameTeam(place=" + this.f40184a + ", form=" + this.f40185b + ", win=" + this.f40186c + ")";
    }
}
