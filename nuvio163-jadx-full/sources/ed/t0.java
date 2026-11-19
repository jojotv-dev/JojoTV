package ed;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class t0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f15327a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final List f15328b;

    public t0(String str, List items) {
        Intrinsics.checkNotNullParameter(items, "items");
        this.f15327a = str;
        this.f15328b = items;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof t0)) {
            return false;
        }
        t0 t0Var = (t0) obj;
        return Intrinsics.areEqual(this.f15327a, t0Var.f15327a) && Intrinsics.areEqual(this.f15328b, t0Var.f15328b);
    }

    public final int hashCode() {
        String str = this.f15327a;
        return this.f15328b.hashCode() + ((str == null ? 0 : str.hashCode()) * 31);
    }

    public final String toString() {
        return "TmdbMovieCollection(name=" + this.f15327a + ", items=" + this.f15328b + ")";
    }
}
