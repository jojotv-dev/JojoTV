package qd;

import java.util.Map;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class e0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Map f46310a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f46311b;

    public e0(long j10, Map ratings) {
        Intrinsics.checkNotNullParameter(ratings, "ratings");
        this.f46310a = ratings;
        this.f46311b = j10;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof e0)) {
            return false;
        }
        e0 e0Var = (e0) obj;
        return Intrinsics.areEqual(this.f46310a, e0Var.f46310a) && this.f46311b == e0Var.f46311b;
    }

    public final int hashCode() {
        return Long.hashCode(this.f46311b) + (this.f46310a.hashCode() * 31);
    }

    public final String toString() {
        return "CacheEntry(ratings=" + this.f46310a + ", expiresAtMs=" + this.f46311b + ")";
    }
}
