package x2;

/* JADX INFO: loaded from: classes3.dex */
public final class n1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final long f63212a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f63213b;

    public n1(long j10, long j11) {
        this.f63212a = j10;
        this.f63213b = j11;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof n1) {
            n1 n1Var = (n1) obj;
            return t3.m.a(this.f63212a, n1Var.f63212a) && this.f63213b == n1Var.f63213b;
        }
        return false;
    }

    public final int hashCode() {
        return Long.hashCode(this.f63213b) + (Long.hashCode(this.f63212a) * 31);
    }
}
