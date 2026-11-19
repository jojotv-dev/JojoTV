package o;

import androidx.appcompat.widget.ActionBarOverlayLayout;

/* JADX INFO: loaded from: classes.dex */
public final class c implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f43105b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ ActionBarOverlayLayout f43106c;

    public /* synthetic */ c(ActionBarOverlayLayout actionBarOverlayLayout, int i10) {
        this.f43105b = i10;
        this.f43106c = actionBarOverlayLayout;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f43105b) {
            case 0:
                ActionBarOverlayLayout actionBarOverlayLayout = this.f43106c;
                actionBarOverlayLayout.e();
                actionBarOverlayLayout.E = actionBarOverlayLayout.f2009i.animate().translationY(0.0f).setListener(actionBarOverlayLayout.F);
                break;
            default:
                ActionBarOverlayLayout actionBarOverlayLayout2 = this.f43106c;
                actionBarOverlayLayout2.e();
                actionBarOverlayLayout2.E = actionBarOverlayLayout2.f2009i.animate().translationY(-actionBarOverlayLayout2.f2009i.getHeight()).setListener(actionBarOverlayLayout2.F);
                break;
        }
    }
}
