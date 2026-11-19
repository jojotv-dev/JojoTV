package ye;

import androidx.lifecycle.g1;
import androidx.lifecycle.h1;

/* JADX INFO: loaded from: classes4.dex */
public final class l implements h1 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ j f65288b;

    public l(j jVar) {
        this.f65288b = jVar;
    }

    @Override // androidx.lifecycle.h1
    public final g1 getViewModelStore() {
        j jVar = this.f65288b;
        g1 g1Var = jVar.f65283a;
        if (g1Var != null) {
            return g1Var;
        }
        g1 g1Var2 = new g1();
        jVar.f65283a = g1Var2;
        return g1Var2;
    }
}
