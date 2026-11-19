package n;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import com.jojo.tv.R;

/* JADX INFO: loaded from: classes3.dex */
public class v {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Context f39397a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final l f39398b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final boolean f39399c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final int f39400d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public View f39401e;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public boolean f39403g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public w f39404h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public t f39405i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public PopupWindow.OnDismissListener f39406j;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public int f39402f = 8388611;
    public final u k = new u(this);

    public v(Context context, l lVar, View view, boolean z2, int i10, int i11) {
        this.f39397a = context;
        this.f39398b = lVar;
        this.f39401e = view;
        this.f39399c = z2;
        this.f39400d = i10;
    }

    public final void a() {
        if (c()) {
            this.f39405i.dismiss();
        }
    }

    public final t b() {
        t c0Var;
        if (this.f39405i == null) {
            Context context = this.f39397a;
            Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
            Point point = new Point();
            defaultDisplay.getRealSize(point);
            if (Math.min(point.x, point.y) >= context.getResources().getDimensionPixelSize(R.dimen.abc_cascading_menus_min_smallest_width)) {
                c0Var = new f(context, this.f39401e, this.f39400d, this.f39399c);
            } else {
                c0Var = new c0(this.f39397a, this.f39398b, this.f39401e, this.f39400d, this.f39399c);
            }
            c0Var.j(this.f39398b);
            c0Var.r(this.k);
            c0Var.n(this.f39401e);
            c0Var.i(this.f39404h);
            c0Var.o(this.f39403g);
            c0Var.p(this.f39402f);
            this.f39405i = c0Var;
        }
        return this.f39405i;
    }

    public final boolean c() {
        t tVar = this.f39405i;
        return tVar != null && tVar.b();
    }

    public void d() {
        this.f39405i = null;
        PopupWindow.OnDismissListener onDismissListener = this.f39406j;
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
    }

    public final void e(boolean z2) {
        this.f39403g = z2;
        t tVar = this.f39405i;
        if (tVar != null) {
            tVar.o(z2);
        }
    }

    public final void f() {
        if (c()) {
            return;
        }
        if (this.f39401e == null) {
            throw new IllegalStateException("MenuPopupHelper cannot be used without an anchor");
        }
        g(0, 0, false, false);
    }

    public final void g(int i10, int i11, boolean z2, boolean z10) {
        t tVarB = b();
        tVarB.s(z10);
        if (z2) {
            if ((Gravity.getAbsoluteGravity(this.f39402f, this.f39401e.getLayoutDirection()) & 7) == 5) {
                i10 -= this.f39401e.getWidth();
            }
            tVarB.q(i10);
            tVarB.t(i11);
            int i12 = (int) ((this.f39397a.getResources().getDisplayMetrics().density * 48.0f) / 2.0f);
            tVarB.f39395b = new Rect(i10 - i12, i11 - i12, i10 + i12, i11 + i12);
        }
        tVarB.k();
    }
}
