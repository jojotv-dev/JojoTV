package fe;

import java.util.HashSet;
import java.util.Set;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class g0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Set f16876a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Set f16877b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final HashSet f16878c;

    public g0(Set channels, Set groups, HashSet kept) {
        Intrinsics.checkNotNullParameter(channels, "channels");
        Intrinsics.checkNotNullParameter(groups, "groups");
        Intrinsics.checkNotNullParameter(kept, "kept");
        this.f16876a = channels;
        this.f16877b = groups;
        this.f16878c = kept;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof g0)) {
            return false;
        }
        g0 g0Var = (g0) obj;
        return Intrinsics.areEqual(this.f16876a, g0Var.f16876a) && Intrinsics.areEqual(this.f16877b, g0Var.f16877b) && Intrinsics.areEqual(this.f16878c, g0Var.f16878c);
    }

    public final int hashCode() {
        return this.f16878c.hashCode() + dc.d1.c(this.f16877b, this.f16876a.hashCode() * 31, 31);
    }

    public final String toString() {
        return "HiddenView(channels=" + this.f16876a + ", groups=" + this.f16877b + ", kept=" + this.f16878c + ")";
    }
}
