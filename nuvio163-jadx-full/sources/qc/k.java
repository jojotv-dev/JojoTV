package qc;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class k extends n {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f45838a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Integer f45839b;

    public k(String message, Integer num) {
        Intrinsics.checkNotNullParameter(message, "message");
        this.f45838a = message;
        this.f45839b = num;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof k)) {
            return false;
        }
        k kVar = (k) obj;
        return Intrinsics.areEqual(this.f45838a, kVar.f45838a) && Intrinsics.areEqual(this.f45839b, kVar.f45839b);
    }

    public final int hashCode() {
        int iHashCode = this.f45838a.hashCode() * 31;
        Integer num = this.f45839b;
        return iHashCode + (num == null ? 0 : num.hashCode());
    }

    public final String toString() {
        return "Error(message=" + this.f45838a + ", code=" + this.f45839b + ")";
    }
}
