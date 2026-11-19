package d7;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class h {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final h f11852c;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final c f11853a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final c f11854b;

    static {
        b bVar = b.f11837a;
        f11852c = new h(bVar, bVar);
    }

    public h(c cVar, c cVar2) {
        this.f11853a = cVar;
        this.f11854b = cVar2;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof h)) {
            return false;
        }
        h hVar = (h) obj;
        return Intrinsics.areEqual(this.f11853a, hVar.f11853a) && Intrinsics.areEqual(this.f11854b, hVar.f11854b);
    }

    public final int hashCode() {
        return this.f11854b.hashCode() + (this.f11853a.hashCode() * 31);
    }

    public final String toString() {
        return "Size(width=" + this.f11853a + ", height=" + this.f11854b + ')';
    }
}
