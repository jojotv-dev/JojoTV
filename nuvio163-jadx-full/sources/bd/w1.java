package bd;

import java.util.Set;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class w1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f5647a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Set f5648b;

    public w1(int i10, Set features) {
        Intrinsics.checkNotNullParameter(features, "features");
        this.f5647a = i10;
        this.f5648b = features;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof w1)) {
            return false;
        }
        w1 w1Var = (w1) obj;
        return this.f5647a == w1Var.f5647a && Intrinsics.areEqual(this.f5648b, w1Var.f5648b);
    }

    public final int hashCode() {
        return this.f5648b.hashCode() + (Integer.hashCode(this.f5647a) * 31);
    }

    public final String toString() {
        return "ProfileSettingsSupplementalChange(profileId=" + this.f5647a + ", features=" + this.f5648b + ")";
    }
}
