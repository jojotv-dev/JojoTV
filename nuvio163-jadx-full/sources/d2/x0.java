package d2;

import android.graphics.RenderEffect;

/* JADX INFO: loaded from: classes.dex */
public abstract class x0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public RenderEffect f11776a;

    public final RenderEffect a() {
        RenderEffect renderEffect = this.f11776a;
        if (renderEffect != null) {
            return renderEffect;
        }
        RenderEffect renderEffectB = b();
        this.f11776a = renderEffectB;
        return renderEffectB;
    }

    public abstract RenderEffect b();
}
