package rd;

import java.util.ArrayList;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class l1 implements o1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final ArrayList f52297a;

    public l1(ArrayList items) {
        Intrinsics.checkNotNullParameter(items, "items");
        this.f52297a = items;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof l1) && Intrinsics.areEqual(this.f52297a, ((l1) obj).f52297a);
    }

    public final int hashCode() {
        return this.f52297a.hashCode();
    }

    public final String toString() {
        return "AddToHistory(items=" + this.f52297a + ")";
    }
}
