package ue;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class a4 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f58438a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final List f58439b;

    public a4(String label, List colorStops) {
        Intrinsics.checkNotNullParameter(label, "label");
        Intrinsics.checkNotNullParameter(colorStops, "colorStops");
        this.f58438a = label;
        this.f58439b = colorStops;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof a4)) {
            return false;
        }
        a4 a4Var = (a4) obj;
        return Intrinsics.areEqual(this.f58438a, a4Var.f58438a) && Intrinsics.areEqual(this.f58439b, a4Var.f58439b);
    }

    public final int hashCode() {
        return this.f58439b.hashCode() + (this.f58438a.hashCode() * 31);
    }

    public final String toString() {
        return "MemberBadgeStyle(label=" + this.f58438a + ", colorStops=" + this.f58439b + ")";
    }
}
