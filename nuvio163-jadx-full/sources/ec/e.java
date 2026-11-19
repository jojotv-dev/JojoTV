package ec;

import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class e {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final boolean f14236a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Function0 f14237b;

    public e(boolean z2, Function0 toggle) {
        Intrinsics.checkNotNullParameter(toggle, "toggle");
        this.f14236a = z2;
        this.f14237b = toggle;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof e)) {
            return false;
        }
        e eVar = (e) obj;
        return this.f14236a == eVar.f14236a && Intrinsics.areEqual(this.f14237b, eVar.f14237b);
    }

    public final int hashCode() {
        return this.f14237b.hashCode() + (Boolean.hashCode(this.f14236a) * 31);
    }

    public final String toString() {
        return "CalendarBell(on=" + this.f14236a + ", toggle=" + this.f14237b + ")";
    }
}
