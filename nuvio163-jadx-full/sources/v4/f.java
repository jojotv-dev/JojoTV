package v4;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class f {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f60655a;

    public f(String name) {
        Intrinsics.checkNotNullParameter(name, "name");
        this.f60655a = name;
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof f)) {
            return false;
        }
        return Intrinsics.areEqual(this.f60655a, ((f) obj).f60655a);
    }

    public final int hashCode() {
        return this.f60655a.hashCode();
    }

    public final String toString() {
        return this.f60655a;
    }
}
