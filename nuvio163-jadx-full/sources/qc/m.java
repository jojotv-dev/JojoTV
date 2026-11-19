package qc;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class m extends n {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Object f45841a;

    public m(Object obj) {
        this.f45841a = obj;
    }

    public final Object a() {
        return this.f45841a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof m) && Intrinsics.areEqual(this.f45841a, ((m) obj).f45841a);
    }

    public final int hashCode() {
        Object obj = this.f45841a;
        if (obj == null) {
            return 0;
        }
        return obj.hashCode();
    }

    public final String toString() {
        return "Success(data=" + this.f45841a + ")";
    }
}
