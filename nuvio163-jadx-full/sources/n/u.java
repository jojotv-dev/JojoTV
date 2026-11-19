package n;

import android.widget.PopupWindow;

/* JADX INFO: loaded from: classes3.dex */
public final class u implements PopupWindow.OnDismissListener {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ v f39396b;

    public u(v vVar) {
        this.f39396b = vVar;
    }

    @Override // android.widget.PopupWindow.OnDismissListener
    public final void onDismiss() {
        this.f39396b.d();
    }
}
