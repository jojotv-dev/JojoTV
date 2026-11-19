package n;

import android.view.View;
import android.view.ViewTreeObserver;
import java.util.ArrayList;
import java.util.Iterator;
import o.k2;
import o.o0;
import o.r0;

/* JADX INFO: loaded from: classes3.dex */
public final class d implements ViewTreeObserver.OnGlobalLayoutListener {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f39297b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f39298c;

    public /* synthetic */ d(Object obj, int i10) {
        this.f39297b = i10;
        this.f39298c = obj;
    }

    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
    public final void onGlobalLayout() {
        switch (this.f39297b) {
            case 0:
                f fVar = (f) this.f39298c;
                ArrayList arrayList = fVar.f39309p;
                if (fVar.b() && arrayList.size() > 0 && !((e) arrayList.get(0)).f39300a.F) {
                    View view = fVar.f39316w;
                    if (view != null && view.isShown()) {
                        Iterator it = arrayList.iterator();
                        while (it.hasNext()) {
                            ((e) it.next()).f39300a.k();
                        }
                    } else {
                        fVar.dismiss();
                    }
                    break;
                }
                break;
            case 1:
                c0 c0Var = (c0) this.f39298c;
                k2 k2Var = c0Var.f39286p;
                if (c0Var.b() && !k2Var.F) {
                    View view2 = c0Var.f39291u;
                    if (view2 != null && view2.isShown()) {
                        k2Var.k();
                    } else {
                        c0Var.dismiss();
                    }
                    break;
                }
                break;
            case 2:
                r0 r0Var = (r0) this.f39298c;
                if (!r0Var.getInternalPopup().b()) {
                    r0Var.f43296n.h(r0Var.getTextDirection(), r0Var.getTextAlignment());
                }
                ViewTreeObserver viewTreeObserver = r0Var.getViewTreeObserver();
                if (viewTreeObserver != null) {
                    viewTreeObserver.removeOnGlobalLayoutListener(this);
                }
                break;
            default:
                o0 o0Var = (o0) this.f39298c;
                r0 r0Var2 = o0Var.N;
                o0Var.getClass();
                if (r0Var2.isAttachedToWindow() && r0Var2.getGlobalVisibleRect(o0Var.L)) {
                    o0Var.p();
                    o0Var.k();
                } else {
                    o0Var.dismiss();
                }
                break;
        }
    }
}
