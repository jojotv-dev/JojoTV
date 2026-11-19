package qd;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class t2 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f47747a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f47748b;

    public t2(int i10, String progressKey) {
        Intrinsics.checkNotNullParameter(progressKey, "progressKey");
        this.f47747a = i10;
        this.f47748b = progressKey;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof t2)) {
            return false;
        }
        t2 t2Var = (t2) obj;
        return this.f47747a == t2Var.f47747a && Intrinsics.areEqual(this.f47748b, t2Var.f47748b);
    }

    public final int hashCode() {
        return this.f47748b.hashCode() + (Integer.hashCode(this.f47747a) * 31);
    }

    public final String toString() {
        return "RemoteProgressWriteKey(profileId=" + this.f47747a + ", progressKey=" + this.f47748b + ")";
    }
}
