package qd;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class d2 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Integer f46204a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f46205b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Integer f46206c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final Long f46207d;

    public d2(Integer num, String str, Integer num2, Long l7, int i10) {
        num = (i10 & 1) != 0 ? null : num;
        str = (i10 & 2) != 0 ? null : str;
        num2 = (i10 & 4) != 0 ? null : num2;
        l7 = (i10 & 8) != 0 ? null : l7;
        this.f46204a = num;
        this.f46205b = str;
        this.f46206c = num2;
        this.f46207d = l7;
    }

    public final String a() {
        return this.f46205b;
    }

    public final Long b() {
        return this.f46207d;
    }

    public final Integer c() {
        return this.f46206c;
    }

    public final Integer d() {
        return this.f46204a;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof d2)) {
            return false;
        }
        d2 d2Var = (d2) obj;
        return Intrinsics.areEqual(this.f46204a, d2Var.f46204a) && Intrinsics.areEqual(this.f46205b, d2Var.f46205b) && Intrinsics.areEqual(this.f46206c, d2Var.f46206c) && Intrinsics.areEqual(this.f46207d, d2Var.f46207d);
    }

    public final int hashCode() {
        Integer num = this.f46204a;
        int iHashCode = (num == null ? 0 : num.hashCode()) * 31;
        String str = this.f46205b;
        int iHashCode2 = (iHashCode + (str == null ? 0 : str.hashCode())) * 31;
        Integer num2 = this.f46206c;
        int iHashCode3 = (iHashCode2 + (num2 == null ? 0 : num2.hashCode())) * 31;
        Long l7 = this.f46207d;
        return iHashCode3 + (l7 != null ? l7.hashCode() : 0);
    }

    public final String toString() {
        return "ParsedContentIds(trakt=" + this.f46204a + ", imdb=" + this.f46205b + ", tmdb=" + this.f46206c + ", simkl=" + this.f46207d + ")";
    }
}
