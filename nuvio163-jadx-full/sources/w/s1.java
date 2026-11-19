package w;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class s1 implements r1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Object f61399a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Object f61400b;

    public s1(Object obj, Object obj2) {
        this.f61399a = obj;
        this.f61400b = obj2;
    }

    @Override // w.r1
    public final Object a() {
        return this.f61400b;
    }

    @Override // w.r1
    public final Object c() {
        return this.f61399a;
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof r1)) {
            return false;
        }
        r1 r1Var = (r1) obj;
        return Intrinsics.areEqual(this.f61399a, r1Var.c()) && Intrinsics.areEqual(this.f61400b, r1Var.a());
    }

    public final int hashCode() {
        Object obj = this.f61399a;
        int iHashCode = (obj != null ? obj.hashCode() : 0) * 31;
        Object obj2 = this.f61400b;
        return iHashCode + (obj2 != null ? obj2.hashCode() : 0);
    }
}
