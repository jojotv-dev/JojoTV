package qd;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class u7 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Object f47882a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f47883b;

    public u7(Object obj, long j10) {
        this.f47882a = obj;
        this.f47883b = j10;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof u7)) {
            return false;
        }
        u7 u7Var = (u7) obj;
        return Intrinsics.areEqual(this.f47882a, u7Var.f47882a) && this.f47883b == u7Var.f47883b;
    }

    public final int hashCode() {
        Object obj = this.f47882a;
        return Long.hashCode(this.f47883b) + ((obj == null ? 0 : obj.hashCode()) * 31);
    }

    public final String toString() {
        return "TimedCache(value=" + this.f47882a + ", updatedAtMs=" + this.f47883b + ")";
    }
}
