package k5;

import java.util.LinkedHashMap;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class l {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f27900a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f27901b;

    static {
        new LinkedHashMap();
    }

    public l(String key, String value) {
        Intrinsics.checkNotNullParameter(key, "key");
        Intrinsics.checkNotNullParameter(value, "value");
        this.f27900a = key;
        this.f27901b = value;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!Intrinsics.areEqual(l.class, obj != null ? obj.getClass() : null)) {
            return false;
        }
        Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type androidx.metrics.performance.StateInfo");
        l lVar = (l) obj;
        return Intrinsics.areEqual(this.f27900a, lVar.f27900a) && Intrinsics.areEqual(this.f27901b, lVar.f27901b);
    }

    public final int hashCode() {
        return this.f27901b.hashCode() + (this.f27900a.hashCode() * 31);
    }

    public final String toString() {
        return this.f27900a + ": " + this.f27901b;
    }
}
