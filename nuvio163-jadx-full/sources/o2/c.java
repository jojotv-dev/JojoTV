package o2;

import android.view.KeyEvent;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final KeyEvent f43445a;

    public final boolean equals(Object obj) {
        if (obj instanceof c) {
            return Intrinsics.areEqual(this.f43445a, ((c) obj).f43445a);
        }
        return false;
    }

    public final int hashCode() {
        return this.f43445a.hashCode();
    }

    public final String toString() {
        return "KeyEvent(nativeKeyEvent=" + this.f43445a + ')';
    }
}
