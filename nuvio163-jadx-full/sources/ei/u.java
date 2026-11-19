package ei;

import dc.d1;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes3.dex */
public final class u {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final List f15718a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final List f15719b;

    public u(List operations, List followedBy) {
        Intrinsics.checkNotNullParameter(operations, "operations");
        Intrinsics.checkNotNullParameter(followedBy, "followedBy");
        this.f15718a = operations;
        this.f15719b = followedBy;
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder();
        sb2.append(CollectionsKt.s(this.f15718a, ", ", null, null, null, 62));
        sb2.append('(');
        return d1.i(sb2, CollectionsKt.s(this.f15719b, ";", null, null, null, 62), ')');
    }
}
