package nf;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class y0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final List f42692a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f42693b;

    public y0(List cues, String rawBody) {
        Intrinsics.checkNotNullParameter(cues, "cues");
        Intrinsics.checkNotNullParameter(rawBody, "rawBody");
        this.f42692a = cues;
        this.f42693b = rawBody;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof y0)) {
            return false;
        }
        y0 y0Var = (y0) obj;
        return Intrinsics.areEqual(this.f42692a, y0Var.f42692a) && Intrinsics.areEqual(this.f42693b, y0Var.f42693b);
    }

    public final int hashCode() {
        return this.f42693b.hashCode() + (this.f42692a.hashCode() * 31);
    }

    public final String toString() {
        return "CachedParsedSubtitle(cues=" + this.f42692a + ", rawBody=" + this.f42693b + ")";
    }
}
