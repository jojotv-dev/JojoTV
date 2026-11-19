package r7;

import java.util.Iterator;

/* JADX INFO: loaded from: classes4.dex */
public final class p0 extends q0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ Iterable[] f51333b;

    public p0(Iterable[] iterableArr) {
        this.f51333b = iterableArr;
    }

    @Override // java.lang.Iterable
    public final Iterator iterator() {
        o0 o0Var = new o0(this, this.f51333b.length);
        r1 r1Var = new r1();
        r1Var.f51347c = q1.f51342i;
        r1Var.f51348e = o0Var;
        return r1Var;
    }
}
