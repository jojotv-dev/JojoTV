package r7;

/* JADX INFO: loaded from: classes4.dex */
public final class g2 extends op.b implements q7.i {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final g2 f51262e = new g2(l0.f51299i, l0.f51298e);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final m0 f51263b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final m0 f51264c;

    public g2(m0 m0Var, m0 m0Var2) {
        this.f51263b = m0Var;
        this.f51264c = m0Var2;
        if (m0Var.compareTo(m0Var2) > 0 || m0Var == l0.f51298e || m0Var2 == l0.f51299i) {
            StringBuilder sb2 = new StringBuilder("Invalid range: ");
            StringBuilder sb3 = new StringBuilder(16);
            m0Var.b(sb3);
            sb3.append("..");
            m0Var2.c(sb3);
            sb2.append(sb3.toString());
            throw new IllegalArgumentException(sb2.toString());
        }
    }

    @Override // q7.i
    public final boolean apply(Object obj) {
        Comparable comparable = (Comparable) obj;
        comparable.getClass();
        return this.f51263b.e(comparable) && !this.f51264c.e(comparable);
    }

    public final boolean equals(Object obj) {
        if (obj instanceof g2) {
            g2 g2Var = (g2) obj;
            if (this.f51263b.equals(g2Var.f51263b) && this.f51264c.equals(g2Var.f51264c)) {
                return true;
            }
        }
        return false;
    }

    public final int hashCode() {
        return this.f51264c.hashCode() + (this.f51263b.hashCode() * 31);
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder(16);
        this.f51263b.b(sb2);
        sb2.append("..");
        this.f51264c.c(sb2);
        return sb2.toString();
    }
}
