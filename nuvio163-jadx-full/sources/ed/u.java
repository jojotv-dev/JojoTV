package ed;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class u {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final List f15329a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final boolean f15330b;

    public u(List items, boolean z2) {
        Intrinsics.checkNotNullParameter(items, "items");
        this.f15329a = items;
        this.f15330b = z2;
    }

    public final List a() {
        return this.f15329a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof u)) {
            return false;
        }
        u uVar = (u) obj;
        return Intrinsics.areEqual(this.f15329a, uVar.f15329a) && this.f15330b == uVar.f15330b;
    }

    public final int hashCode() {
        return Boolean.hashCode(this.f15330b) + (this.f15329a.hashCode() * 31);
    }

    public final String toString() {
        return "TmdbEntityRailPageResult(items=" + this.f15329a + ", hasMore=" + this.f15330b + ")";
    }
}
