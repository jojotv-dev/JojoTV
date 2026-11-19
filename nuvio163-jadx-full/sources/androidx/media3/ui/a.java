package androidx.media3.ui;

import android.view.View;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class a implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3758b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ View f3759c;

    public /* synthetic */ a(View view, int i10) {
        this.f3758b = i10;
        this.f3759c = view;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3758b) {
            case 0:
                ((DefaultTimeBar) this.f3759c).lambda$new$0();
                break;
            case 1:
                ((PlayerControlView) this.f3759c).updateProgress();
                break;
            default:
                ((PlayerView) this.f3759c).invalidate();
                break;
        }
    }
}
