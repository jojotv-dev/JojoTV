package bc;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class o implements r {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Throwable f4440a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final n f4441b;

    public o(Throwable result, n referenceLinkHandler) {
        Intrinsics.checkNotNullParameter(result, "result");
        Intrinsics.checkNotNullParameter(referenceLinkHandler, "referenceLinkHandler");
        this.f4440a = result;
        this.f4441b = referenceLinkHandler;
    }

    @Override // bc.r
    public final n a() {
        return this.f4441b;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof o)) {
            return false;
        }
        o oVar = (o) obj;
        return Intrinsics.areEqual(this.f4440a, oVar.f4440a) && Intrinsics.areEqual(this.f4441b, oVar.f4441b);
    }

    public final int hashCode() {
        return this.f4441b.hashCode() + (this.f4440a.hashCode() * 31);
    }

    public final String toString() {
        return "Error(result=" + this.f4440a + ", referenceLinkHandler=" + this.f4441b + ")";
    }
}
