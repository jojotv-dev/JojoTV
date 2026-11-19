package nf;

import java.util.Arrays;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class f3 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final byte[] f42176a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Long f42177b;

    public f3(byte[] bytes, Long l7) {
        Intrinsics.checkNotNullParameter(bytes, "bytes");
        this.f42176a = bytes;
        this.f42177b = l7;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof f3)) {
            return false;
        }
        f3 f3Var = (f3) obj;
        return Intrinsics.areEqual(this.f42176a, f3Var.f42176a) && Intrinsics.areEqual(this.f42177b, f3Var.f42177b);
    }

    public final int hashCode() {
        int iHashCode = Arrays.hashCode(this.f42176a) * 31;
        Long l7 = this.f42177b;
        return iHashCode + (l7 == null ? 0 : l7.hashCode());
    }

    public final String toString() {
        return "RangeResponse(bytes=" + Arrays.toString(this.f42176a) + ", totalLength=" + this.f42177b + ")";
    }
}
