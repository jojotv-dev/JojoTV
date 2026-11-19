package x2;

import android.os.Build;
import android.view.ViewConfiguration;

/* JADX INFO: loaded from: classes.dex */
public final class t0 implements w2 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final ViewConfiguration f63280a;

    public t0(ViewConfiguration viewConfiguration) {
        this.f63280a = viewConfiguration;
    }

    @Override // x2.w2
    public final long a() {
        return ViewConfiguration.getDoubleTapTimeout();
    }

    @Override // x2.w2
    public final long b() {
        return ViewConfiguration.getLongPressTimeout();
    }

    @Override // x2.w2
    public final float c() {
        if (Build.VERSION.SDK_INT >= 34) {
            return u0.b(this.f63280a);
        }
        return 2.0f;
    }

    @Override // x2.w2
    public final float e() {
        return this.f63280a.getScaledMaximumFlingVelocity();
    }

    @Override // x2.w2
    public final float f() {
        return this.f63280a.getScaledTouchSlop();
    }

    @Override // x2.w2
    public final float g() {
        if (Build.VERSION.SDK_INT >= 34) {
            return u0.a(this.f63280a);
        }
        return 16.0f;
    }
}
