package qd;

import java.util.ArrayList;
import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class z9 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final ArrayList f48430a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f48431b;

    public z9(long j10, ArrayList items) {
        Intrinsics.checkNotNullParameter(items, "items");
        this.f48430a = items;
        this.f48431b = j10;
    }

    public final List a() {
        return this.f48430a;
    }

    public final long b() {
        return this.f48431b;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof z9)) {
            return false;
        }
        z9 z9Var = (z9) obj;
        return Intrinsics.areEqual(this.f48430a, z9Var.f48430a) && this.f48431b == z9Var.f48431b;
    }

    public final int hashCode() {
        return Long.hashCode(this.f48431b) + (this.f48430a.hashCode() * 31);
    }

    public final String toString() {
        return "TimedCache(items=" + this.f48430a + ", updatedAtMs=" + this.f48431b + ")";
    }
}
