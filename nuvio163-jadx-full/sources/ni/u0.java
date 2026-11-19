package ni;

import java.util.List;
import kf.vd;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* JADX INFO: loaded from: classes3.dex */
public abstract class u0 implements li.h {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final li.h f42860a;

    public u0(li.h hVar) {
        this.f42860a = hVar;
    }

    @Override // li.h
    public final boolean b() {
        return false;
    }

    @Override // li.h
    public final int c(String name) {
        Intrinsics.checkNotNullParameter(name, "name");
        Integer intOrNull = StringsKt.toIntOrNull(name);
        if (intOrNull != null) {
            return intOrNull.intValue();
        }
        throw new IllegalArgumentException(w.h0.x(name, " is not a valid list index"));
    }

    @Override // li.h
    public final int d() {
        return 1;
    }

    @Override // li.h
    public final String e(int i10) {
        return String.valueOf(i10);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof u0)) {
            return false;
        }
        u0 u0Var = (u0) obj;
        return Intrinsics.areEqual(this.f42860a, u0Var.f42860a) && Intrinsics.areEqual(h(), u0Var.h());
    }

    @Override // li.h
    public final List f(int i10) {
        if (i10 >= 0) {
            return CollectionsKt.emptyList();
        }
        StringBuilder sbV = o2.a.v(i10, "Illegal index ", ", ");
        sbV.append(h());
        sbV.append(" expects only non-negative indices");
        throw new IllegalArgumentException(sbV.toString().toString());
    }

    @Override // li.h
    public final li.h g(int i10) {
        if (i10 >= 0) {
            return this.f42860a;
        }
        StringBuilder sbV = o2.a.v(i10, "Illegal index ", ", ");
        sbV.append(h());
        sbV.append(" expects only non-negative indices");
        throw new IllegalArgumentException(sbV.toString().toString());
    }

    @Override // li.h
    public final List getAnnotations() {
        return CollectionsKt.emptyList();
    }

    @Override // li.h
    public final vd getKind() {
        return li.n.f34122l;
    }

    public final int hashCode() {
        return h().hashCode() + (this.f42860a.hashCode() * 31);
    }

    @Override // li.h
    public final boolean i(int i10) {
        if (i10 >= 0) {
            return false;
        }
        StringBuilder sbV = o2.a.v(i10, "Illegal index ", ", ");
        sbV.append(h());
        sbV.append(" expects only non-negative indices");
        throw new IllegalArgumentException(sbV.toString().toString());
    }

    @Override // li.h
    public final boolean isInline() {
        return false;
    }

    public final String toString() {
        return h() + '(' + this.f42860a + ')';
    }
}
