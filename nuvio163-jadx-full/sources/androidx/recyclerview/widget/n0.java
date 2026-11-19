package androidx.recyclerview.widget;

import android.view.View;
import android.view.ViewGroup;

/* JADX INFO: loaded from: classes.dex */
public final class n0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f4003a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Object f4004b;

    public /* synthetic */ n0(Object obj, int i10) {
        this.f4003a = i10;
        this.f4004b = obj;
    }

    public int a(View view) {
        int right;
        int i10;
        switch (this.f4003a) {
            case 0:
                q0 q0Var = (q0) view.getLayoutParams();
                right = view.getRight() + ((q0) view.getLayoutParams()).f4075b.right;
                i10 = ((ViewGroup.MarginLayoutParams) q0Var).rightMargin;
                break;
            default:
                q0 q0Var2 = (q0) view.getLayoutParams();
                right = view.getBottom() + ((q0) view.getLayoutParams()).f4075b.bottom;
                i10 = ((ViewGroup.MarginLayoutParams) q0Var2).bottomMargin;
                break;
        }
        return right + i10;
    }

    public int b(View view) {
        int left;
        int i10;
        switch (this.f4003a) {
            case 0:
                q0 q0Var = (q0) view.getLayoutParams();
                left = view.getLeft() - ((q0) view.getLayoutParams()).f4075b.left;
                i10 = ((ViewGroup.MarginLayoutParams) q0Var).leftMargin;
                break;
            default:
                q0 q0Var2 = (q0) view.getLayoutParams();
                left = view.getTop() - ((q0) view.getLayoutParams()).f4075b.top;
                i10 = ((ViewGroup.MarginLayoutParams) q0Var2).topMargin;
                break;
        }
        return left - i10;
    }

    public int c() {
        int i10;
        int iC;
        switch (this.f4003a) {
            case 0:
                p0 p0Var = (p0) this.f4004b;
                i10 = p0Var.f4062n;
                iC = p0Var.C();
                break;
            default:
                p0 p0Var2 = (p0) this.f4004b;
                i10 = p0Var2.f4063o;
                iC = p0Var2.A();
                break;
        }
        return i10 - iC;
    }

    public int d() {
        switch (this.f4003a) {
            case 0:
                return ((p0) this.f4004b).B();
            default:
                return ((p0) this.f4004b).D();
        }
    }
}
