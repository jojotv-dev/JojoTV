package g1;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class o0 implements v1.f {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final q f18512b;

    public o0(q qVar) {
        this.f18512b = qVar;
    }

    public final boolean equals(Object obj) {
        if (obj instanceof o0) {
            return Intrinsics.areEqual(this.f18512b, ((o0) obj).f18512b);
        }
        return false;
    }

    public final int hashCode() {
        return this.f18512b.hashCode() * 31;
    }
}
