package rd;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class v5 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final List f52633a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f52634b;

    public v5(List items, long j10) {
        Intrinsics.checkNotNullParameter(items, "items");
        this.f52633a = items;
        this.f52634b = j10;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof v5)) {
            return false;
        }
        v5 v5Var = (v5) obj;
        return Intrinsics.areEqual(this.f52633a, v5Var.f52633a) && this.f52634b == v5Var.f52634b;
    }

    public final int hashCode() {
        return Long.hashCode(this.f52634b) + (this.f52633a.hashCode() * 31);
    }

    public final String toString() {
        return "TimedCache(items=" + this.f52633a + ", updatedAtMs=" + this.f52634b + ")";
    }
}
