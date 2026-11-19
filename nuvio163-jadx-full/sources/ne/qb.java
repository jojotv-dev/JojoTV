package ne;

import java.util.Set;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class qb {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f41376a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Set f41377b;

    public qb(String str, Set mark) {
        Intrinsics.checkNotNullParameter(mark, "mark");
        this.f41376a = str;
        this.f41377b = mark;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof qb)) {
            return false;
        }
        qb qbVar = (qb) obj;
        return Intrinsics.areEqual(this.f41376a, qbVar.f41376a) && Intrinsics.areEqual(this.f41377b, qbVar.f41377b);
    }

    public final int hashCode() {
        String str = this.f41376a;
        return this.f41377b.hashCode() + ((str == null ? 0 : str.hashCode()) * 31);
    }

    public final String toString() {
        return "TableRequest(leagueId=" + this.f41376a + ", mark=" + this.f41377b + ")";
    }
}
