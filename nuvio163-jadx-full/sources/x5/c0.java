package x5;

import android.graphics.Matrix;
import android.view.View;

/* JADX INFO: loaded from: classes3.dex */
public final class c0 extends b0 {
    @Override // wf.l
    public final float e(View view) {
        return view.getTransitionAlpha();
    }

    @Override // wf.l
    public final void j(View view, float f10) {
        view.setTransitionAlpha(f10);
    }

    @Override // x5.b0, wf.l
    public final void k(int i10, View view) {
        view.setTransitionVisibility(i10);
    }

    @Override // x5.b0
    public final void l(View view, int i10, int i11, int i12, int i13) {
        view.setLeftTopRightBottom(i10, i11, i12, i13);
    }

    @Override // x5.b0
    public final void m(View view, Matrix matrix) {
        view.transformMatrixToGlobal(matrix);
    }

    @Override // x5.b0
    public final void n(View view, Matrix matrix) {
        view.transformMatrixToLocal(matrix);
    }
}
