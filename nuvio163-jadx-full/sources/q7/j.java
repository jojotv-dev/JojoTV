package q7;

import java.io.Serializable;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public final class j implements i, Serializable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final List f45750b;

    public j(List list) {
        this.f45750b = list;
    }

    @Override // q7.i
    public final boolean apply(Object obj) {
        int i10 = 0;
        while (true) {
            List list = this.f45750b;
            if (i10 >= list.size()) {
                return true;
            }
            if (!((i) list.get(i10)).apply(obj)) {
                return false;
            }
            i10++;
        }
    }

    public final boolean equals(Object obj) {
        if (obj instanceof j) {
            return this.f45750b.equals(((j) obj).f45750b);
        }
        return false;
    }

    public final int hashCode() {
        return this.f45750b.hashCode() + 306654252;
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder("Predicates.and(");
        boolean z2 = true;
        for (Object obj : this.f45750b) {
            if (!z2) {
                sb2.append(',');
            }
            sb2.append(obj);
            z2 = false;
        }
        sb2.append(')');
        return sb2.toString();
    }
}
