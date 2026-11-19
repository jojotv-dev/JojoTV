package bd;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class f5 extends h5 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Exception f4812a;

    public f5(Exception throwable) {
        Intrinsics.checkNotNullParameter(throwable, "throwable");
        this.f4812a = throwable;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof f5) && Intrinsics.areEqual(this.f4812a, ((f5) obj).f4812a);
    }

    public final int hashCode() {
        return this.f4812a.hashCode();
    }

    public final String toString() {
        return "Failure(throwable=" + this.f4812a + ")";
    }
}
