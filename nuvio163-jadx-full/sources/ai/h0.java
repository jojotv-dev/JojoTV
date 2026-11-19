package ai;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes3.dex */
public final class h0 implements s1, ei.c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public Integer f1761a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public Integer f1762b;

    public h0(Integer num, Integer num2) {
        this.f1761a = num;
        this.f1762b = num2;
    }

    @Override // ai.s1
    public final Integer A() {
        return this.f1762b;
    }

    @Override // ei.c
    public final Object copy() {
        return new h0(this.f1761a, this.f1762b);
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof h0)) {
            return false;
        }
        h0 h0Var = (h0) obj;
        return Intrinsics.areEqual(this.f1761a, h0Var.f1761a) && Intrinsics.areEqual(this.f1762b, h0Var.f1762b);
    }

    @Override // ai.s1
    public final Integer getYear() {
        return this.f1761a;
    }

    public final int hashCode() {
        Integer num = this.f1761a;
        int iHashCode = (num != null ? num.hashCode() : 0) * 31;
        Integer num2 = this.f1762b;
        return iHashCode + (num2 != null ? num2.hashCode() : 0);
    }

    @Override // ai.s1
    public final void s(Integer num) {
        this.f1762b = num;
    }

    @Override // ai.s1
    public final void setYear(Integer num) {
        this.f1761a = num;
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder();
        Object obj = this.f1761a;
        if (obj == null) {
            obj = "??";
        }
        sb2.append(obj);
        sb2.append('-');
        Integer num = this.f1762b;
        sb2.append(num != null ? num : "??");
        return sb2.toString();
    }
}
