package mf;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class al {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final float f35699a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Long f35700b;

    public al(float f10, Long l7) {
        this.f35699a = f10;
        this.f35700b = l7;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof al)) {
            return false;
        }
        al alVar = (al) obj;
        return Float.compare(this.f35699a, alVar.f35699a) == 0 && Intrinsics.areEqual(this.f35700b, alVar.f35700b);
    }

    public final int hashCode() {
        int iHashCode = Float.hashCode(this.f35699a) * 31;
        Long l7 = this.f35700b;
        return iHashCode + (l7 == null ? 0 : l7.hashCode());
    }

    public final String toString() {
        return "SubtitleLiftDecision(appliedLiftPx=" + this.f35699a + ", pendingDropCueVersion=" + this.f35700b + ")";
    }
}
