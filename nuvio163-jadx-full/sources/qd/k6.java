package qd;

import java.util.List;
import java.util.Map;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class k6 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final List f46940a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Map f46941b;

    public k6(List tabs, Map entriesByList) {
        Intrinsics.checkNotNullParameter(tabs, "tabs");
        Intrinsics.checkNotNullParameter(entriesByList, "entriesByList");
        this.f46940a = tabs;
        this.f46941b = entriesByList;
    }

    public final Map a() {
        return this.f46941b;
    }

    public final List b() {
        return this.f46940a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof k6)) {
            return false;
        }
        k6 k6Var = (k6) obj;
        return Intrinsics.areEqual(this.f46940a, k6Var.f46940a) && Intrinsics.areEqual(this.f46941b, k6Var.f46941b);
    }

    public final int hashCode() {
        return this.f46941b.hashCode() + (this.f46940a.hashCode() * 31);
    }

    public final String toString() {
        return "PersonalListFetchResult(tabs=" + this.f46940a + ", entriesByList=" + this.f46941b + ")";
    }
}
