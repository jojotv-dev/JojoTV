package rc;

/* JADX INFO: loaded from: classes4.dex */
public final class i1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f51613a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f51614b;

    public i1(long j10, int i10) {
        this.f51613a = j10;
        this.f51614b = i10;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof i1)) {
            return false;
        }
        i1 i1Var = (i1) obj;
        return this.f51613a == i1Var.f51613a && this.f51614b == i1Var.f51614b;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f51614b) + (Long.hashCode(this.f51613a) * 31);
    }

    public final String toString() {
        return "Vint(value=" + this.f51613a + ", length=" + this.f51614b + ")";
    }
}
