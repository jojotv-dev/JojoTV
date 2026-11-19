package mf;

import java.util.Map;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class kl {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f36890a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Map f36891b;

    public kl(String url, Map map) {
        Intrinsics.checkNotNullParameter(url, "url");
        this.f36890a = url;
        this.f36891b = map;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof kl)) {
            return false;
        }
        kl klVar = (kl) obj;
        return Intrinsics.areEqual(this.f36890a, klVar.f36890a) && Intrinsics.areEqual(this.f36891b, klVar.f36891b);
    }

    public final int hashCode() {
        int iHashCode = this.f36890a.hashCode() * 31;
        Map map = this.f36891b;
        return iHashCode + (map == null ? 0 : map.hashCode());
    }

    public final String toString() {
        return "SubtitleRoute(url=" + this.f36890a + ", headers=" + this.f36891b + ")";
    }
}
