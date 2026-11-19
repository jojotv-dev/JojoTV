package fc;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class b {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f16242a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final d2.h f16243b;

    public b(String drawableName, d2.h icon) {
        Intrinsics.checkNotNullParameter(drawableName, "drawableName");
        Intrinsics.checkNotNullParameter(icon, "icon");
        this.f16242a = drawableName;
        this.f16243b = icon;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof b)) {
            return false;
        }
        b bVar = (b) obj;
        return Intrinsics.areEqual(this.f16242a, bVar.f16242a) && Intrinsics.areEqual(this.f16243b, bVar.f16243b);
    }

    public final int hashCode() {
        return this.f16243b.hashCode() + (this.f16242a.hashCode() * 31);
    }

    public final String toString() {
        return "IconPackEntry(drawableName=" + this.f16242a + ", icon=" + this.f16243b + ")";
    }
}
