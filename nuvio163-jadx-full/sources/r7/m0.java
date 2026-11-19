package r7;

import java.io.Serializable;

/* JADX INFO: loaded from: classes4.dex */
public abstract class m0 implements Comparable, Serializable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Object f51309b;

    public m0(Comparable comparable) {
        this.f51309b = comparable;
    }

    /* JADX WARN: Type inference failed for: r0v3, types: [java.lang.Comparable, java.lang.Object] */
    @Override // java.lang.Comparable
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public int compareTo(m0 m0Var) {
        if (m0Var == l0.f51299i) {
            return 1;
        }
        if (m0Var == l0.f51298e) {
            return -1;
        }
        Object obj = m0Var.f51309b;
        g2 g2Var = g2.f51262e;
        int iCompareTo = this.f51309b.compareTo(obj);
        return iCompareTo != 0 ? iCompareTo : Boolean.compare(false, false);
    }

    public abstract void b(StringBuilder sb2);

    public abstract void c(StringBuilder sb2);

    /* JADX WARN: Type inference failed for: r0v0, types: [java.lang.Comparable, java.lang.Object] */
    public Comparable d() {
        return this.f51309b;
    }

    public abstract boolean e(Comparable comparable);

    public final boolean equals(Object obj) {
        if (obj instanceof m0) {
            try {
                if (compareTo((m0) obj) == 0) {
                    return true;
                }
            } catch (ClassCastException unused) {
            }
        }
        return false;
    }

    public abstract int hashCode();
}
