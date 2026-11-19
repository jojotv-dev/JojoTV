package sf;

/* JADX INFO: loaded from: classes4.dex */
public final class de {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final boolean f54738a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final md.a0 f54739b;

    public de(md.a0 a0Var, int i10) {
        boolean z2 = (i10 & 1) == 0;
        a0Var = (i10 & 2) != 0 ? null : a0Var;
        this.f54738a = z2;
        this.f54739b = a0Var;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof de)) {
            return false;
        }
        de deVar = (de) obj;
        return this.f54738a == deVar.f54738a && this.f54739b == deVar.f54739b;
    }

    public final int hashCode() {
        int iHashCode = Boolean.hashCode(this.f54738a) * 31;
        md.a0 a0Var = this.f54739b;
        return iHashCode + (a0Var == null ? 0 : a0Var.hashCode());
    }

    public final String toString() {
        return "ServerSignInState(connecting=" + this.f54738a + ", error=" + this.f54739b + ")";
    }
}
