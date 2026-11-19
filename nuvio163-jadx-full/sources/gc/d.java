package gc;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f18872a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f18873b;

    public d(int i10, String body) {
        Intrinsics.checkNotNullParameter(body, "body");
        this.f18872a = i10;
        this.f18873b = body;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof d)) {
            return false;
        }
        d dVar = (d) obj;
        return this.f18872a == dVar.f18872a && Intrinsics.areEqual(this.f18873b, dVar.f18873b);
    }

    public final int hashCode() {
        return this.f18873b.hashCode() + (Integer.hashCode(this.f18872a) * 31);
    }

    public final String toString() {
        return "AuthHttpResponse(statusCode=" + this.f18872a + ", body=" + this.f18873b + ")";
    }
}
