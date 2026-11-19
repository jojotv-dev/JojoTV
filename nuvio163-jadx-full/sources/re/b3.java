package re;

import java.util.Map;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class b3 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f52827a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Map f52828b;

    public b3(String url, Map headers) {
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(headers, "headers");
        this.f52827a = url;
        this.f52828b = headers;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof b3)) {
            return false;
        }
        b3 b3Var = (b3) obj;
        return Intrinsics.areEqual(this.f52827a, b3Var.f52827a) && Intrinsics.areEqual(this.f52828b, b3Var.f52828b);
    }

    public final int hashCode() {
        return this.f52828b.hashCode() + (this.f52827a.hashCode() * 31);
    }

    public final String toString() {
        return "LiveTvVodPlayback(url=" + this.f52827a + ", headers=" + this.f52828b + ")";
    }
}
