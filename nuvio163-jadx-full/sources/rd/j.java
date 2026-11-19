package rd;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class j {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final List f52211a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final boolean f52212b;

    public j(List seasons, boolean z2) {
        Intrinsics.checkNotNullParameter(seasons, "seasons");
        this.f52211a = seasons;
        this.f52212b = z2;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof j)) {
            return false;
        }
        j jVar = (j) obj;
        return Intrinsics.areEqual(this.f52211a, jVar.f52211a) && this.f52212b == jVar.f52212b;
    }

    public final int hashCode() {
        return Boolean.hashCode(this.f52212b) + (this.f52211a.hashCode() * 31);
    }

    public final String toString() {
        return "EpisodeUpdate(seasons=" + this.f52211a + ", wasAlreadyWatched=" + this.f52212b + ")";
    }
}
