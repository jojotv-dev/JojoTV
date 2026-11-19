package nf;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class e3 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f42157a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Long f42158b;

    public e3(Long l7, long j10) {
        this.f42157a = j10;
        this.f42158b = l7;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof e3)) {
            return false;
        }
        e3 e3Var = (e3) obj;
        return this.f42157a == e3Var.f42157a && Intrinsics.areEqual(this.f42158b, e3Var.f42158b);
    }

    public final int hashCode() {
        int iHashCode = Long.hashCode(this.f42157a) * 31;
        Long l7 = this.f42158b;
        return iHashCode + (l7 == null ? 0 : l7.hashCode());
    }

    public final String toString() {
        return "PendingIndexedCue(startTimeMs=" + this.f42157a + ", explicitDurationMs=" + this.f42158b + ")";
    }
}
