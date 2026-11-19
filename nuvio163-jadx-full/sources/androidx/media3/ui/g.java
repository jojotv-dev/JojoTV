package androidx.media3.ui;

import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class g implements View.OnClickListener {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3772b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f3773c;

    public /* synthetic */ g(Object obj, int i10) {
        this.f3772b = i10;
        this.f3773c = obj;
    }

    @Override // android.view.View.OnClickListener
    public final void onClick(View view) {
        switch (this.f3772b) {
            case 0:
                ((PlayerControlViewLayoutManager) this.f3773c).onOverflowButtonClick(view);
                break;
            default:
                ((PlayerControlView) this.f3773c).onFullscreenButtonClicked(view);
                break;
        }
    }
}
