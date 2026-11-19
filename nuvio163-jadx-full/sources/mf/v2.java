package mf;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class v2 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f38549a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Long f38550b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Long f38551c;

    public v2(long j10, Long l7, Long l10) {
        this.f38549a = j10;
        this.f38550b = l7;
        this.f38551c = l10;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof v2)) {
            return false;
        }
        v2 v2Var = (v2) obj;
        return this.f38549a == v2Var.f38549a && Intrinsics.areEqual(this.f38550b, v2Var.f38550b) && Intrinsics.areEqual(this.f38551c, v2Var.f38551c);
    }

    public final int hashCode() {
        int iHashCode = Long.hashCode(this.f38549a) * 31;
        Long l7 = this.f38550b;
        int iHashCode2 = (iHashCode + (l7 == null ? 0 : l7.hashCode())) * 31;
        Long l10 = this.f38551c;
        return iHashCode2 + (l10 != null ? l10.hashCode() : 0);
    }

    public final String toString() {
        return "TopLevelLayout(segmentDataStart=" + this.f38549a + ", seekHeadPosition=" + this.f38550b + ", chaptersPosition=" + this.f38551c + ")";
    }
}
