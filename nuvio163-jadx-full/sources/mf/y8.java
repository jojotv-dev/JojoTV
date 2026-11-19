package mf;

import java.util.Map;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class y8 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f38956a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Map f38957b;

    public y8(String url, Map headers) {
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(headers, "headers");
        this.f38956a = url;
        this.f38957b = headers;
    }

    public final Map a() {
        return this.f38957b;
    }

    public final String b() {
        return this.f38956a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof y8)) {
            return false;
        }
        y8 y8Var = (y8) obj;
        return Intrinsics.areEqual(this.f38956a, y8Var.f38956a) && Intrinsics.areEqual(this.f38957b, y8Var.f38957b);
    }

    public final int hashCode() {
        return this.f38957b.hashCode() + (this.f38956a.hashCode() * 31);
    }

    public final String toString() {
        return "NormalizedPlaybackRequest(url=" + this.f38956a + ", headers=" + this.f38957b + ")";
    }
}
