package r7;

import java.io.Serializable;

/* JADX INFO: loaded from: classes4.dex */
public final class p2 extends f2 implements Serializable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final f2 f51339b;

    public p2(f2 f2Var) {
        this.f51339b = f2Var;
    }

    @Override // r7.f2
    public final f2 a() {
        return this.f51339b;
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        return this.f51339b.compare(obj2, obj);
    }

    @Override // java.util.Comparator
    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof p2) {
            return this.f51339b.equals(((p2) obj).f51339b);
        }
        return false;
    }

    public final int hashCode() {
        return -this.f51339b.hashCode();
    }

    public final String toString() {
        return this.f51339b + ".reverse()";
    }
}
