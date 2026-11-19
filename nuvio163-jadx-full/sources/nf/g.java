package nf;

/* JADX INFO: loaded from: classes4.dex */
public final class g {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final double f42178a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final double f42179b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final double f42180c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final int f42181d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final boolean f42182e;

    public g(double d10, double d11, double d12, int i10, boolean z2) {
        this.f42178a = d10;
        this.f42179b = d11;
        this.f42180c = d12;
        this.f42181d = i10;
        this.f42182e = z2;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof g)) {
            return false;
        }
        g gVar = (g) obj;
        return Double.compare(this.f42178a, gVar.f42178a) == 0 && Double.compare(this.f42179b, gVar.f42179b) == 0 && Double.compare(this.f42180c, gVar.f42180c) == 0 && this.f42181d == gVar.f42181d && this.f42182e == gVar.f42182e;
    }

    public final int hashCode() {
        return Boolean.hashCode(this.f42182e) + w.h0.a(this.f42181d, dc.d1.a(dc.d1.a(Double.hashCode(this.f42178a) * 31, 31, this.f42179b), 31, this.f42180c), 31);
    }

    public final String toString() {
        return "AutoSyncDelayOnlyAlignment(offsetMs=" + this.f42178a + ", score=" + this.f42179b + ", margin=" + this.f42180c + ", segmentsPassed=" + this.f42181d + ", stableSegmentMarginOverride=" + this.f42182e + ")";
    }
}
