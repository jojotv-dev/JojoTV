package androidx.media3.ui;

import android.animation.ValueAnimator;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class h implements ValueAnimator.AnimatorUpdateListener {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f3774a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ Object f3775b;

    public /* synthetic */ h(Object obj, int i10) {
        this.f3774a = i10;
        this.f3775b = obj;
    }

    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
        switch (this.f3774a) {
            case 0:
                ((PlayerControlViewLayoutManager) this.f3775b).lambda$new$0(valueAnimator);
                break;
            case 1:
                ((PlayerControlViewLayoutManager) this.f3775b).lambda$new$1(valueAnimator);
                break;
            case 2:
                ((PlayerControlViewLayoutManager) this.f3775b).lambda$new$2(valueAnimator);
                break;
            case 3:
                ((PlayerControlViewLayoutManager) this.f3775b).lambda$new$3(valueAnimator);
                break;
            default:
                ((DefaultTimeBar) this.f3775b).lambda$new$1(valueAnimator);
                break;
        }
    }
}
