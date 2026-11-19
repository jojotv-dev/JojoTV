package ye;

import androidx.lifecycle.g1;
import androidx.lifecycle.z0;

/* JADX INFO: loaded from: classes4.dex */
public final class j extends z0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public g1 f65283a;

    @Override // androidx.lifecycle.z0
    public final void onCleared() {
        g1 g1Var = this.f65283a;
        if (g1Var != null) {
            g1Var.a();
        }
        this.f65283a = null;
    }
}
