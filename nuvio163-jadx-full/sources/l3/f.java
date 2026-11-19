package l3;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class f {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Object f31860a;

    public final boolean equals(Object obj) {
        if (obj instanceof f) {
            return Intrinsics.areEqual(this.f31860a, ((f) obj).f31860a);
        }
        return false;
    }

    public final int hashCode() {
        Object obj = this.f31860a;
        if (obj == null) {
            return 0;
        }
        return obj.hashCode();
    }

    public final String toString() {
        return "AsyncTypefaceResult(result=" + this.f31860a + ')';
    }
}
