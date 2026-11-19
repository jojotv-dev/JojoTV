package v1;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class n {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f60635a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Integer f60636b;

    public n(int i10, Integer num) {
        this.f60635a = i10;
        this.f60636b = num;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof n)) {
            return false;
        }
        n nVar = (n) obj;
        return this.f60635a == nVar.f60635a && Intrinsics.areEqual(this.f60636b, nVar.f60636b);
    }

    public final int hashCode() {
        int iHashCode = Integer.hashCode(this.f60635a) * 31;
        Integer num = this.f60636b;
        return iHashCode + (num == null ? 0 : num.hashCode());
    }

    public final String toString() {
        return "ObjectLocation(group=" + this.f60635a + ", dataOffset=" + this.f60636b + ')';
    }
}
