package rd;

/* JADX INFO: loaded from: classes.dex */
public final class i1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f52178a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f52179b;

    public i1(int i10, long j10) {
        this.f52178a = i10;
        this.f52179b = j10;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof i1)) {
            return false;
        }
        i1 i1Var = (i1) obj;
        return this.f52178a == i1Var.f52178a && this.f52179b == i1Var.f52179b;
    }

    public final int hashCode() {
        return Long.hashCode(this.f52179b) + (Integer.hashCode(this.f52178a) * 31);
    }

    public final String toString() {
        StringBuilder sbM = bd.e0.m(this.f52179b, "SimklAuthScope(profileId=", ", generation=", this.f52178a);
        sbM.append(")");
        return sbM.toString();
    }
}
