package nf;

import java.util.ArrayList;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class f1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f42172a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final ArrayList f42173b;

    public f1(String url, ArrayList headers) {
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(headers, "headers");
        this.f42172a = url;
        this.f42173b = headers;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof f1)) {
            return false;
        }
        f1 f1Var = (f1) obj;
        return Intrinsics.areEqual(this.f42172a, f1Var.f42172a) && Intrinsics.areEqual(this.f42173b, f1Var.f42173b);
    }

    public final int hashCode() {
        return this.f42173b.hashCode() + (this.f42172a.hashCode() * 31);
    }

    public final String toString() {
        return "ParsedSubtitleCacheKey(url=" + this.f42172a + ", headers=" + this.f42173b + ")";
    }
}
