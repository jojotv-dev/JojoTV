package nf;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f42114a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Integer f42115b;

    public c(String name, Integer num) {
        Intrinsics.checkNotNullParameter(name, "name");
        this.f42114a = name;
        this.f42115b = num;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof c)) {
            return false;
        }
        c cVar = (c) obj;
        return Intrinsics.areEqual(this.f42114a, cVar.f42114a) && Intrinsics.areEqual(this.f42115b, cVar.f42115b);
    }

    public final int hashCode() {
        int iHashCode = this.f42114a.hashCode() * 31;
        Integer num = this.f42115b;
        return iHashCode + (num == null ? 0 : num.hashCode());
    }

    public final String toString() {
        return "Style(name=" + this.f42114a + ", alignment=" + this.f42115b + ")";
    }
}
