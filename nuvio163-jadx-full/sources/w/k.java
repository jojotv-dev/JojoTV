package w;

import g1.i3;

/* JADX INFO: loaded from: classes.dex */
public final class k implements i3 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final z1 f61303b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final g1.x1 f61304c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public p f61305e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public long f61306i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public long f61307m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public boolean f61308n;

    public /* synthetic */ k(z1 z1Var, Object obj, p pVar, int i10) {
        this(z1Var, obj, (i10 & 4) != 0 ? null : pVar, Long.MIN_VALUE, Long.MIN_VALUE, false);
    }

    @Override // g1.i3
    public final Object getValue() {
        return this.f61304c.getValue();
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder("AnimationState(value=");
        sb2.append(this.f61304c.getValue());
        sb2.append(", velocity=");
        sb2.append(this.f61303b.f61480b.invoke(this.f61305e));
        sb2.append(", isRunning=");
        sb2.append(this.f61308n);
        sb2.append(", lastFrameTimeNanos=");
        sb2.append(this.f61306i);
        sb2.append(", finishedTimeNanos=");
        return bd.e0.j(sb2, this.f61307m, ')');
    }

    public k(z1 z1Var, Object obj, p pVar, long j10, long j11, boolean z2) {
        p pVarE;
        this.f61303b = z1Var;
        this.f61304c = g1.i.v(obj);
        if (pVar != null) {
            pVarE = d.e(pVar);
        } else {
            pVarE = (p) z1Var.f61479a.invoke(obj);
            pVarE.d();
        }
        this.f61305e = pVarE;
        this.f61306i = j10;
        this.f61307m = j11;
        this.f61308n = z2;
    }
}
