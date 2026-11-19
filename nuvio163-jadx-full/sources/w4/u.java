package w4;

import android.util.SparseArray;

/* JADX INFO: loaded from: classes.dex */
public final class u {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final SparseArray f62021a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public x f62022b;

    public u(int i10) {
        this.f62021a = new SparseArray(i10);
    }

    public final void a(x xVar, int i10, int i11) {
        int iA = xVar.a(i10);
        SparseArray sparseArray = this.f62021a;
        u uVar = sparseArray == null ? null : (u) sparseArray.get(iA);
        if (uVar == null) {
            uVar = new u(1);
            sparseArray.put(xVar.a(i10), uVar);
        }
        if (i11 > i10) {
            uVar.a(xVar, i10 + 1, i11);
        } else {
            uVar.f62022b = xVar;
        }
    }
}
