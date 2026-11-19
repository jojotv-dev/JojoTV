package l5;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class h {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final z0 f31993a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final boolean f31994b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final boolean f31995c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final Object f31996d;

    public h(z0 type, boolean z2, Object obj, boolean z10) {
        Intrinsics.checkNotNullParameter(type, "type");
        if (!type.f32143a && z2) {
            throw new IllegalArgumentException(type.b().concat(" does not allow nullable values").toString());
        }
        if (!z2 && z10 && obj == null) {
            throw new IllegalArgumentException(("Argument with type " + type.b() + " has null value but is not nullable.").toString());
        }
        this.f31993a = type;
        this.f31994b = z2;
        this.f31996d = obj;
        this.f31995c = z10;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !Intrinsics.areEqual(h.class, obj.getClass())) {
            return false;
        }
        h hVar = (h) obj;
        Object obj2 = hVar.f31996d;
        if (this.f31994b != hVar.f31994b || this.f31995c != hVar.f31995c || !Intrinsics.areEqual(this.f31993a, hVar.f31993a)) {
            return false;
        }
        Object obj3 = this.f31996d;
        if (obj3 != null) {
            return Intrinsics.areEqual(obj3, obj2);
        }
        return obj2 == null;
    }

    public final int hashCode() {
        int iHashCode = ((((this.f31993a.hashCode() * 31) + (this.f31994b ? 1 : 0)) * 31) + (this.f31995c ? 1 : 0)) * 31;
        Object obj = this.f31996d;
        return iHashCode + (obj != null ? obj.hashCode() : 0);
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder();
        sb2.append(h.class.getSimpleName());
        sb2.append(" Type: " + this.f31993a);
        sb2.append(" Nullable: " + this.f31994b);
        if (this.f31995c) {
            sb2.append(" DefaultValue: " + this.f31996d);
        }
        String string = sb2.toString();
        Intrinsics.checkNotNullExpressionValue(string, "sb.toString()");
        return string;
    }
}
