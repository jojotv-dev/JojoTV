package ne;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class v {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Long f41684a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f41685b;

    public v(Long l7, String text) {
        Intrinsics.checkNotNullParameter(text, "text");
        this.f41684a = l7;
        this.f41685b = text;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof v)) {
            return false;
        }
        v vVar = (v) obj;
        return Intrinsics.areEqual(this.f41684a, vVar.f41684a) && Intrinsics.areEqual(this.f41685b, vVar.f41685b);
    }

    public final int hashCode() {
        Long l7 = this.f41684a;
        return this.f41685b.hashCode() + ((l7 == null ? 0 : l7.hashCode()) * 31);
    }

    public final String toString() {
        return "Meeting(dateMs=" + this.f41684a + ", text=" + this.f41685b + ")";
    }
}
