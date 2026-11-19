package x5;

import android.graphics.Matrix;
import android.os.Build;
import android.view.View;

/* JADX INFO: loaded from: classes3.dex */
public class b0 extends wf.l {

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static boolean f63430o = true;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public static boolean f63431p = true;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public static boolean f63432q = true;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public static boolean f63433r = true;

    @Override // wf.l
    public void k(int i10, View view) {
        if (Build.VERSION.SDK_INT == 28) {
            super.k(i10, view);
        } else if (f63433r) {
            try {
                a3.d.D(i10, view);
            } catch (NoSuchMethodError unused) {
                f63433r = false;
            }
        }
    }

    public void l(View view, int i10, int i11, int i12, int i13) {
        if (f63432q) {
            try {
                a3.d.B(view, i10, i11, i12, i13);
            } catch (NoSuchMethodError unused) {
                f63432q = false;
            }
        }
    }

    public void m(View view, Matrix matrix) {
        if (f63430o) {
            try {
                a3.d.H(view, matrix);
            } catch (NoSuchMethodError unused) {
                f63430o = false;
            }
        }
    }

    public void n(View view, Matrix matrix) {
        if (f63431p) {
            try {
                a3.d.I(view, matrix);
            } catch (NoSuchMethodError unused) {
                f63431p = false;
            }
        }
    }
}
