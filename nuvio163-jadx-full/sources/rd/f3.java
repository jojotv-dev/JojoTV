package rd;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class f3 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final List f52048a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final LinkedHashMap f52049b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final ArrayList f52050c;

    public f3(List items, LinkedHashMap itemsByStatus, ArrayList tabs) {
        Intrinsics.checkNotNullParameter(items, "items");
        Intrinsics.checkNotNullParameter(itemsByStatus, "itemsByStatus");
        Intrinsics.checkNotNullParameter(tabs, "tabs");
        this.f52048a = items;
        this.f52049b = itemsByStatus;
        this.f52050c = tabs;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof f3)) {
            return false;
        }
        f3 f3Var = (f3) obj;
        return Intrinsics.areEqual(this.f52048a, f3Var.f52048a) && Intrinsics.areEqual(this.f52049b, f3Var.f52049b) && Intrinsics.areEqual(this.f52050c, f3Var.f52050c);
    }

    public final int hashCode() {
        return this.f52050c.hashCode() + ((this.f52049b.hashCode() + (this.f52048a.hashCode() * 31)) * 31);
    }

    public final String toString() {
        return "SimklLibraryProjection(items=" + this.f52048a + ", itemsByStatus=" + this.f52049b + ", tabs=" + this.f52050c + ")";
    }
}
