package nf;

import java.util.List;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class y2 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final List f42696a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Set f42697b;

    public y2(List cues, Set estimatedEndStartsMs) {
        Intrinsics.checkNotNullParameter(cues, "cues");
        Intrinsics.checkNotNullParameter(estimatedEndStartsMs, "estimatedEndStartsMs");
        this.f42696a = cues;
        this.f42697b = estimatedEndStartsMs;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof y2)) {
            return false;
        }
        y2 y2Var = (y2) obj;
        return Intrinsics.areEqual(this.f42696a, y2Var.f42696a) && Intrinsics.areEqual(this.f42697b, y2Var.f42697b);
    }

    public final int hashCode() {
        return this.f42697b.hashCode() + (this.f42696a.hashCode() * 31);
    }

    public final String toString() {
        return "IndexedSubtitleTimeline(cues=" + this.f42696a + ", estimatedEndStartsMs=" + this.f42697b + ")";
    }
}
