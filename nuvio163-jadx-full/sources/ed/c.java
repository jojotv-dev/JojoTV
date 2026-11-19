package ed;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f15000a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Integer f15001b;

    public c(String title, Integer num) {
        Intrinsics.checkNotNullParameter(title, "title");
        this.f15000a = title;
        this.f15001b = num;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof c)) {
            return false;
        }
        c cVar = (c) obj;
        return Intrinsics.areEqual(this.f15000a, cVar.f15000a) && Intrinsics.areEqual(this.f15001b, cVar.f15001b);
    }

    public final int hashCode() {
        int iHashCode = this.f15000a.hashCode() * 31;
        Integer num = this.f15001b;
        return iHashCode + (num == null ? 0 : num.hashCode());
    }

    public final String toString() {
        return "ParsedTmdbSearchTitle(title=" + this.f15000a + ", year=" + this.f15001b + ")";
    }
}
