package nf;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class w2 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f42654a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Long f42655b;

    public w2(int i10, Long l7) {
        this.f42654a = i10;
        this.f42655b = l7;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof w2)) {
            return false;
        }
        w2 w2Var = (w2) obj;
        return this.f42654a == w2Var.f42654a && Intrinsics.areEqual(this.f42655b, w2Var.f42655b);
    }

    public final int hashCode() {
        int iHashCode = Integer.hashCode(this.f42654a) * 31;
        Long l7 = this.f42655b;
        return iHashCode + (l7 == null ? 0 : l7.hashCode());
    }

    public final String toString() {
        return "CueTrackPosition(trackNumber=" + this.f42654a + ", durationTicks=" + this.f42655b + ")";
    }
}
