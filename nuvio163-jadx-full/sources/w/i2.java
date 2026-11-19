package w;

/* JADX INFO: loaded from: classes.dex */
public final class i2 implements d2 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f61297b;

    public i2(int i10) {
        this.f61297b = i10;
    }

    @Override // w.b2
    public final p e(long j10, p pVar, p pVar2, p pVar3) {
        return j10 < ((long) this.f61297b) * 1000000 ? pVar : pVar2;
    }

    @Override // w.d2
    public final int k() {
        return this.f61297b;
    }

    @Override // w.d2
    public final int l() {
        return 0;
    }

    @Override // w.b2
    public final p g(long j10, p pVar, p pVar2, p pVar3) {
        return pVar3;
    }
}
