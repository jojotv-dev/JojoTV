package ld;

/* JADX INFO: loaded from: classes4.dex */
public final class x {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f33185a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f33186b;

    public x(int i10, long j10) {
        this.f33185a = i10;
        this.f33186b = j10;
    }

    public static x a(x xVar, long j10) {
        int i10 = xVar.f33185a;
        xVar.getClass();
        return new x(i10, j10);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof x)) {
            return false;
        }
        x xVar = (x) obj;
        return this.f33185a == xVar.f33185a && this.f33186b == xVar.f33186b;
    }

    public final int hashCode() {
        return Long.hashCode(this.f33186b) + (Integer.hashCode(this.f33185a) * 31);
    }

    public final String toString() {
        StringBuilder sbM = bd.e0.m(this.f33186b, "MdbListAuthScope(profileId=", ", generation=", this.f33185a);
        sbM.append(")");
        return sbM.toString();
    }
}
