package i0;

/* JADX INFO: loaded from: classes.dex */
public final class k {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f23628a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f23629b;

    public k(int i10, int i11) {
        this.f23628a = i10;
        this.f23629b = i11;
        if (!(i10 >= 0)) {
            d0.b.a("negative start index");
        }
        if (i11 >= i10) {
            return;
        }
        d0.b.a("end index greater than start");
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof k)) {
            return false;
        }
        k kVar = (k) obj;
        return this.f23628a == kVar.f23628a && this.f23629b == kVar.f23629b;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f23629b) + (Integer.hashCode(this.f23628a) * 31);
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder("Interval(start=");
        sb2.append(this.f23628a);
        sb2.append(", end=");
        return o2.a.p(sb2, this.f23629b, ')');
    }
}
