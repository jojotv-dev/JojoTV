package rc;

import android.view.Display;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class t0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Display.Mode f51773a;

    public t0(Display.Mode appliedMode) {
        Intrinsics.checkNotNullParameter(appliedMode, "appliedMode");
        this.f51773a = appliedMode;
    }

    public final Display.Mode a() {
        return this.f51773a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof t0) && Intrinsics.areEqual(this.f51773a, ((t0) obj).f51773a);
    }

    public final int hashCode() {
        return this.f51773a.hashCode();
    }

    public final String toString() {
        return "DisplayModeSwitchResult(appliedMode=" + this.f51773a + ")";
    }
}
