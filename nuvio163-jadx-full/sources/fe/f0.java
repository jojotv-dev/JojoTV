package fe;

import java.util.ArrayList;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class f0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final ArrayList f16817a;

    public f0(ArrayList views) {
        Intrinsics.checkNotNullParameter(views, "views");
        this.f16817a = views;
    }

    public final boolean a(String channelKey, String groupId) {
        Intrinsics.checkNotNullParameter(channelKey, "channelKey");
        Intrinsics.checkNotNullParameter(groupId, "groupId");
        ArrayList<g0> arrayList = this.f16817a;
        if (arrayList.isEmpty()) {
            return false;
        }
        if (arrayList.isEmpty()) {
            return true;
        }
        for (g0 g0Var : arrayList) {
            if (g0Var.f16878c.contains(channelKey)) {
                return false;
            }
            if (!g0Var.f16876a.contains(channelKey) && !g0Var.f16877b.contains(groupId)) {
                return false;
            }
        }
        return true;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof f0) && Intrinsics.areEqual(this.f16817a, ((f0) obj).f16817a);
    }

    public final int hashCode() {
        return this.f16817a.hashCode();
    }

    public final String toString() {
        return "HiddenEverywhere(views=" + this.f16817a + ")";
    }
}
