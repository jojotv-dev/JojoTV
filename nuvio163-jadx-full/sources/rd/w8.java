package rd;

import java.util.LinkedHashSet;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class w8 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final List f52679a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final LinkedHashSet f52680b;

    public w8(LinkedHashSet fullyWatchedSeriesKeys, List items) {
        Intrinsics.checkNotNullParameter(items, "items");
        Intrinsics.checkNotNullParameter(fullyWatchedSeriesKeys, "fullyWatchedSeriesKeys");
        this.f52679a = items;
        this.f52680b = fullyWatchedSeriesKeys;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof w8)) {
            return false;
        }
        w8 w8Var = (w8) obj;
        return Intrinsics.areEqual(this.f52679a, w8Var.f52679a) && Intrinsics.areEqual(this.f52680b, w8Var.f52680b);
    }

    public final int hashCode() {
        return this.f52680b.hashCode() + (this.f52679a.hashCode() * 31);
    }

    public final String toString() {
        return "SimklWatchedProjection(items=" + this.f52679a + ", fullyWatchedSeriesKeys=" + this.f52680b + ")";
    }
}
