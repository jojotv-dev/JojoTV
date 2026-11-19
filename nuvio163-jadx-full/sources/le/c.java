package le;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final boolean f33347a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Long f33348b;

    public c(boolean z2, Long l7) {
        this.f33347a = z2;
        this.f33348b = l7;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof c)) {
            return false;
        }
        c cVar = (c) obj;
        return this.f33347a == cVar.f33347a && Intrinsics.areEqual(this.f33348b, cVar.f33348b);
    }

    public final int hashCode() {
        int iHashCode = Boolean.hashCode(this.f33347a) * 31;
        Long l7 = this.f33348b;
        return iHashCode + (l7 == null ? 0 : l7.hashCode());
    }

    public final String toString() {
        return "EpisodeState(watched=" + this.f33347a + ", resumeMs=" + this.f33348b + ")";
    }
}
