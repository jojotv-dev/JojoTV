package h6;

import android.graphics.Rect;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes3.dex */
public final class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final g6.a f20780a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final float f20781b;

    public a(g6.a _bounds, float f10) {
        Intrinsics.checkNotNullParameter(_bounds, "_bounds");
        this.f20780a = _bounds;
        this.f20781b = f10;
    }

    public final Rect a() {
        g6.a aVar = this.f20780a;
        aVar.getClass();
        return new Rect(aVar.f18810a, aVar.f18811b, aVar.f18812c, aVar.f18813d);
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!Intrinsics.areEqual(a.class, obj != null ? obj.getClass() : null)) {
            return false;
        }
        Intrinsics.checkNotNull(obj, "null cannot be cast to non-null type androidx.window.layout.WindowMetrics");
        a aVar = (a) obj;
        return Intrinsics.areEqual(this.f20780a, aVar.f20780a) && this.f20781b == aVar.f20781b;
    }

    public final int hashCode() {
        return Float.hashCode(this.f20781b) + (this.f20780a.hashCode() * 31);
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder("WindowMetrics(_bounds=");
        sb2.append(this.f20780a);
        sb2.append(", density=");
        return o2.a.o(sb2, this.f20781b, ')');
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public a(Rect bounds, float f10) {
        this(new g6.a(bounds), f10);
        Intrinsics.checkNotNullParameter(bounds, "bounds");
    }
}
