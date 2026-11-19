package t3;

import w.h0;

/* JADX INFO: loaded from: classes.dex */
public final class l {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final l f57165e = new l(0, 0, 0, 0);

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f57166a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f57167b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f57168c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final int f57169d;

    public l(int i10, int i11, int i12, int i13) {
        this.f57166a = i10;
        this.f57167b = i11;
        this.f57168c = i12;
        this.f57169d = i13;
    }

    public final long a() {
        int iD = (d() / 2) + this.f57166a;
        return (((long) ((b() / 2) + this.f57167b)) & 4294967295L) | (((long) iD) << 32);
    }

    public final int b() {
        return this.f57169d - this.f57167b;
    }

    public final long c() {
        return (((long) this.f57166a) << 32) | (((long) this.f57167b) & 4294967295L);
    }

    public final int d() {
        return this.f57168c - this.f57166a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof l)) {
            return false;
        }
        l lVar = (l) obj;
        return this.f57166a == lVar.f57166a && this.f57167b == lVar.f57167b && this.f57168c == lVar.f57168c && this.f57169d == lVar.f57169d;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f57169d) + h0.a(this.f57168c, h0.a(this.f57167b, Integer.hashCode(this.f57166a) * 31, 31), 31);
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder("IntRect.fromLTRB(");
        sb2.append(this.f57166a);
        sb2.append(", ");
        sb2.append(this.f57167b);
        sb2.append(", ");
        sb2.append(this.f57168c);
        sb2.append(", ");
        return o2.a.p(sb2, this.f57169d, ')');
    }
}
