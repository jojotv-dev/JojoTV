package n;

import android.view.ActionProvider;

/* JADX INFO: loaded from: classes3.dex */
public final class o implements ActionProvider.VisibilityListener {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public a3.h f39386a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final ActionProvider f39387b;

    public o(s sVar, ActionProvider actionProvider) {
        this.f39387b = actionProvider;
    }

    public final boolean a() {
        return this.f39387b.hasSubMenu();
    }

    public final void b(d0 d0Var) {
        this.f39387b.onPrepareSubMenu(d0Var);
    }

    @Override // android.view.ActionProvider.VisibilityListener
    public final void onActionProviderVisibilityChanged(boolean z2) {
        a3.h hVar = this.f39386a;
        if (hVar != null) {
            l lVar = ((n) hVar.f582c).f39373n;
            lVar.f39342h = true;
            lVar.p(true);
        }
    }
}
