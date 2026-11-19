package androidx.media3.ui;

import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class j implements View.OnLayoutChangeListener {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f3778a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ Object f3779b;

    public /* synthetic */ j(Object obj, int i10) {
        this.f3778a = i10;
        this.f3779b = obj;
    }

    @Override // android.view.View.OnLayoutChangeListener
    public final void onLayoutChange(View view, int i10, int i11, int i12, int i13, int i14, int i15, int i16, int i17) {
        switch (this.f3778a) {
            case 0:
                ((PlayerControlViewLayoutManager) this.f3779b).onLayoutChange(view, i10, i11, i12, i13, i14, i15, i16, i17);
                break;
            default:
                ((PlayerControlView) this.f3779b).onLayoutChange(view, i10, i11, i12, i13, i14, i15, i16, i17);
                break;
        }
    }
}
