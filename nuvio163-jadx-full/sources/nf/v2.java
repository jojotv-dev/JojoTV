package nf;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class v2 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Long f42635a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Long f42636b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Long f42637c;

    public v2(Long l7, Long l10, Long l11) {
        this.f42635a = l7;
        this.f42636b = l10;
        this.f42637c = l11;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof v2)) {
            return false;
        }
        v2 v2Var = (v2) obj;
        return Intrinsics.areEqual(this.f42635a, v2Var.f42635a) && Intrinsics.areEqual(this.f42636b, v2Var.f42636b) && Intrinsics.areEqual(this.f42637c, v2Var.f42637c);
    }

    public final int hashCode() {
        Long l7 = this.f42635a;
        int iHashCode = (l7 == null ? 0 : l7.hashCode()) * 31;
        Long l10 = this.f42636b;
        int iHashCode2 = (iHashCode + (l10 == null ? 0 : l10.hashCode())) * 31;
        Long l11 = this.f42637c;
        return iHashCode2 + (l11 != null ? l11.hashCode() : 0);
    }

    public final String toString() {
        return "ContentRange(start=" + this.f42635a + ", end=" + this.f42636b + ", total=" + this.f42637c + ")";
    }
}
