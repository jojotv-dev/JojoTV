package w;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class h1 implements b2 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final b2 f61272b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final long f61273c;

    public h1(b2 b2Var, long j10) {
        this.f61272b = b2Var;
        this.f61273c = j10;
    }

    @Override // w.b2
    public final boolean a() {
        return this.f61272b.a();
    }

    @Override // w.b2
    public final long b(p pVar, p pVar2, p pVar3) {
        return this.f61272b.b(pVar, pVar2, pVar3) + this.f61273c;
    }

    @Override // w.b2
    public final p e(long j10, p pVar, p pVar2, p pVar3) {
        long j11 = this.f61273c;
        return j10 < j11 ? pVar : this.f61272b.e(j10 - j11, pVar, pVar2, pVar3);
    }

    public final boolean equals(Object obj) {
        if (!(obj instanceof h1)) {
            return false;
        }
        h1 h1Var = (h1) obj;
        return h1Var.f61273c == this.f61273c && Intrinsics.areEqual(h1Var.f61272b, this.f61272b);
    }

    @Override // w.b2
    public final p g(long j10, p pVar, p pVar2, p pVar3) {
        long j11 = this.f61273c;
        return j10 < j11 ? pVar3 : this.f61272b.g(j10 - j11, pVar, pVar2, pVar3);
    }

    public final int hashCode() {
        return Long.hashCode(this.f61273c) + (this.f61272b.hashCode() * 31);
    }
}
