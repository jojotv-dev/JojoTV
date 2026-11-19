package rc;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class p0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f51731a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Long f51732b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final boolean f51733c;

    public p0(long j10, Long l7, boolean z2) {
        this.f51731a = j10;
        this.f51732b = l7;
        this.f51733c = z2;
    }

    public final Long a() {
        return this.f51732b;
    }

    public final boolean b() {
        return this.f51733c;
    }

    public final long c() {
        return this.f51731a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof p0)) {
            return false;
        }
        p0 p0Var = (p0) obj;
        return this.f51731a == p0Var.f51731a && Intrinsics.areEqual(this.f51732b, p0Var.f51732b) && this.f51733c == p0Var.f51733c;
    }

    public final int hashCode() {
        int iHashCode = Long.hashCode(this.f51731a) * 31;
        Long l7 = this.f51732b;
        return Boolean.hashCode(this.f51733c) + ((iHashCode + (l7 == null ? 0 : l7.hashCode())) * 31);
    }

    public final String toString() {
        return "ExternalPlayerResult(positionMs=" + this.f51731a + ", durationMs=" + this.f51732b + ", endedByUser=" + this.f51733c + ")";
    }
}
