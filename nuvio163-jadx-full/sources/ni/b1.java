package ni;

import java.util.List;
import kf.vd;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

/* JADX INFO: loaded from: classes3.dex */
public abstract class b1 implements li.h {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f42747a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final li.h f42748b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final li.h f42749c;

    public b1(String str, li.h hVar, li.h hVar2) {
        this.f42747a = str;
        this.f42748b = hVar;
        this.f42749c = hVar2;
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
        throw new IllegalArgumentException(w.h0.x(name, " is not a valid map index"));
    }

    @Override // li.h
    public final int d() {
        return 2;
    }

    @Override // li.h
    public final String e(int i10) {
        return String.valueOf(i10);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof b1)) {
            return false;
        }
        b1 b1Var = (b1) obj;
        return Intrinsics.areEqual(this.f42747a, b1Var.f42747a) && Intrinsics.areEqual(this.f42748b, b1Var.f42748b) && Intrinsics.areEqual(this.f42749c, b1Var.f42749c);
    }

    @Override // li.h
    public final List f(int i10) {
        if (i10 >= 0) {
            return CollectionsKt.emptyList();
        }
        throw new IllegalArgumentException(o2.a.q(o2.a.v(i10, "Illegal index ", ", "), this.f42747a, " expects only non-negative indices").toString());
    }

    @Override // li.h
    public final li.h g(int i10) {
        if (i10 < 0) {
            throw new IllegalArgumentException(o2.a.q(o2.a.v(i10, "Illegal index ", ", "), this.f42747a, " expects only non-negative indices").toString());
        }
        int i11 = i10 % 2;
        if (i11 == 0) {
            return this.f42748b;
        }
        if (i11 == 1) {
            return this.f42749c;
        }
        throw new IllegalStateException("Unreached");
    }

    @Override // li.h
    public final List getAnnotations() {
        return CollectionsKt.emptyList();
    }

    @Override // li.h
    public final vd getKind() {
        return li.n.f34123m;
    }

    @Override // li.h
    public final String h() {
        return this.f42747a;
    }

    public final int hashCode() {
        return this.f42749c.hashCode() + ((this.f42748b.hashCode() + (this.f42747a.hashCode() * 31)) * 31);
    }

    @Override // li.h
    public final boolean i(int i10) {
        if (i10 >= 0) {
            return false;
        }
        throw new IllegalArgumentException(o2.a.q(o2.a.v(i10, "Illegal index ", ", "), this.f42747a, " expects only non-negative indices").toString());
    }

    @Override // li.h
    public final boolean isInline() {
        return false;
    }

    public final String toString() {
        return this.f42747a + '(' + this.f42748b + ", " + this.f42749c + ')';
    }
}
