package androidx.recyclerview.widget;

import android.util.SparseArray;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public final class u0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public SparseArray f4102a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f4103b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public Set f4104c;

    public final t0 a(int i10) {
        SparseArray sparseArray = this.f4102a;
        t0 t0Var = (t0) sparseArray.get(i10);
        if (t0Var != null) {
            return t0Var;
        }
        t0 t0Var2 = new t0();
        sparseArray.put(i10, t0Var2);
        return t0Var2;
    }
}
