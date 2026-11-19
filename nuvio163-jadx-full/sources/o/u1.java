package o;

import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;

/* JADX INFO: loaded from: classes3.dex */
public final class u1 implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f43334b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ v1 f43335c;

    public /* synthetic */ u1(v1 v1Var, int i10) {
        this.f43334b = i10;
        this.f43335c = v1Var;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f43334b) {
            case 0:
                ViewParent parent = this.f43335c.f43341i.getParent();
                if (parent != null) {
                    parent.requestDisallowInterceptTouchEvent(true);
                }
                break;
            default:
                v1 v1Var = this.f43335c;
                v1Var.a();
                View view = v1Var.f43341i;
                if (view.isEnabled() && !view.isLongClickable() && v1Var.c()) {
                    view.getParent().requestDisallowInterceptTouchEvent(true);
                    long jUptimeMillis = SystemClock.uptimeMillis();
                    MotionEvent motionEventObtain = MotionEvent.obtain(jUptimeMillis, jUptimeMillis, 3, 0.0f, 0.0f, 0);
                    view.onTouchEvent(motionEventObtain);
                    motionEventObtain.recycle();
                    v1Var.f43344o = true;
                    break;
                }
                break;
        }
    }
}
