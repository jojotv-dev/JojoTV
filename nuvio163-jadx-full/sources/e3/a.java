package e3;

import kotlin.Function;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f13809a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Function f13810b;

    public a(String str, Function function) {
        this.f13809a = str;
        this.f13810b = function;
    }

    public final String a() {
        return this.f13809a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof a)) {
            return false;
        }
        a aVar = (a) obj;
        return Intrinsics.areEqual(this.f13809a, aVar.f13809a) && Intrinsics.areEqual(this.f13810b, aVar.f13810b);
    }

    public final int hashCode() {
        String str = this.f13809a;
        int iHashCode = (str != null ? str.hashCode() : 0) * 31;
        Function function = this.f13810b;
        return iHashCode + (function != null ? function.hashCode() : 0);
    }

    public final String toString() {
        return "AccessibilityAction(label=" + this.f13809a + ", action=" + this.f13810b + ')';
    }
}
