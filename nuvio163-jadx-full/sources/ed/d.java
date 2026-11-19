package ed;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Object f15014a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Object f15015b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Object f15016c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final Object f15017d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final Object f15018e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final Object f15019f;

    public d(Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
        this.f15014a = obj;
        this.f15015b = obj2;
        this.f15016c = obj3;
        this.f15017d = obj4;
        this.f15018e = obj5;
        this.f15019f = obj6;
    }

    public final Object a() {
        return this.f15014a;
    }

    public final Object b() {
        return this.f15015b;
    }

    public final Object c() {
        return this.f15016c;
    }

    public final Object d() {
        return this.f15017d;
    }

    public final Object e() {
        return this.f15018e;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof d)) {
            return false;
        }
        d dVar = (d) obj;
        return Intrinsics.areEqual(this.f15014a, dVar.f15014a) && Intrinsics.areEqual(this.f15015b, dVar.f15015b) && Intrinsics.areEqual(this.f15016c, dVar.f15016c) && Intrinsics.areEqual(this.f15017d, dVar.f15017d) && Intrinsics.areEqual(this.f15018e, dVar.f15018e) && Intrinsics.areEqual(this.f15019f, dVar.f15019f);
    }

    public final Object f() {
        return this.f15019f;
    }

    public final int hashCode() {
        Object obj = this.f15014a;
        int iHashCode = (obj == null ? 0 : obj.hashCode()) * 31;
        Object obj2 = this.f15015b;
        int iHashCode2 = (iHashCode + (obj2 == null ? 0 : obj2.hashCode())) * 31;
        Object obj3 = this.f15016c;
        int iHashCode3 = (iHashCode2 + (obj3 == null ? 0 : obj3.hashCode())) * 31;
        Object obj4 = this.f15017d;
        int iHashCode4 = (iHashCode3 + (obj4 == null ? 0 : obj4.hashCode())) * 31;
        Object obj5 = this.f15018e;
        int iHashCode5 = (iHashCode4 + (obj5 == null ? 0 : obj5.hashCode())) * 31;
        Object obj6 = this.f15019f;
        return iHashCode5 + (obj6 != null ? obj6.hashCode() : 0);
    }

    public final String toString() {
        return "Sextuple(first=" + this.f15014a + ", second=" + this.f15015b + ", third=" + this.f15016c + ", fourth=" + this.f15017d + ", fifth=" + this.f15018e + ", sixth=" + this.f15019f + ")";
    }
}
