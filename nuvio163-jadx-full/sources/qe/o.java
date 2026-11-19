package qe;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class o {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f49567a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Integer f49568b;

    public o(String str, Integer num) {
        this.f49567a = str;
        this.f49568b = num;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof o)) {
            return false;
        }
        o oVar = (o) obj;
        return Intrinsics.areEqual(this.f49567a, oVar.f49567a) && Intrinsics.areEqual(this.f49568b, oVar.f49568b);
    }

    public final int hashCode() {
        String str = this.f49567a;
        int iHashCode = (str == null ? 0 : str.hashCode()) * 31;
        Integer num = this.f49568b;
        return iHashCode + (num != null ? num.hashCode() : 0);
    }

    public final String toString() {
        return "DescriptionParts(text=" + this.f49567a + ", year=" + this.f49568b + ")";
    }
}
