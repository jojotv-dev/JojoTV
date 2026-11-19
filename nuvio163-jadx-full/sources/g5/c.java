package g5;

import java.util.LinkedHashMap;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public abstract class c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final LinkedHashMap f18805a = new LinkedHashMap();

    public abstract Object a(b bVar);

    public final boolean equals(Object obj) {
        if (obj instanceof c) {
            return Intrinsics.areEqual(this.f18805a, ((c) obj).f18805a);
        }
        return false;
    }

    public final int hashCode() {
        return this.f18805a.hashCode();
    }

    public final String toString() {
        return "CreationExtras(extras=" + this.f18805a + ')';
    }
}
