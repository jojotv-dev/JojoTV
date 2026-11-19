package md;

import java.util.ArrayList;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class b1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final ArrayList f35027a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Integer f35028b;

    public b1(ArrayList items, Integer num) {
        Intrinsics.checkNotNullParameter(items, "items");
        this.f35027a = items;
        this.f35028b = num;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof b1)) {
            return false;
        }
        b1 b1Var = (b1) obj;
        return Intrinsics.areEqual(this.f35027a, b1Var.f35027a) && Intrinsics.areEqual(this.f35028b, b1Var.f35028b);
    }

    public final int hashCode() {
        return this.f35028b.hashCode() + (this.f35027a.hashCode() * 31);
    }

    public final String toString() {
        return "ServerPage(items=" + this.f35027a + ", totalCount=" + this.f35028b + ")";
    }
}
