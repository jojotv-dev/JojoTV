package nf;

import java.util.Map;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class j {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final i f42264a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Map f42265b;

    public j(i iVar, Map evidenceByReferenceKey) {
        Intrinsics.checkNotNullParameter(evidenceByReferenceKey, "evidenceByReferenceKey");
        this.f42264a = iVar;
        this.f42265b = evidenceByReferenceKey;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof j)) {
            return false;
        }
        j jVar = (j) obj;
        return Intrinsics.areEqual(this.f42264a, jVar.f42264a) && Intrinsics.areEqual(this.f42265b, jVar.f42265b);
    }

    public final int hashCode() {
        i iVar = this.f42264a;
        return this.f42265b.hashCode() + ((iVar == null ? 0 : iVar.hashCode()) * 31);
    }

    public final String toString() {
        return "Result(best=" + this.f42264a + ", evidenceByReferenceKey=" + this.f42265b + ")";
    }
}
