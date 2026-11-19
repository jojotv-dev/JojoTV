package d2;

import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.RectF;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class l implements u0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Path f11742a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public RectF f11743b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public float[] f11744c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public Matrix f11745d;

    public l(Path path) {
        this.f11742a = path;
    }

    public final c2.c c() {
        if (this.f11743b == null) {
            this.f11743b = new RectF();
        }
        RectF rectF = this.f11743b;
        Intrinsics.checkNotNull(rectF);
        this.f11742a.computeBounds(rectF, true);
        return new c2.c(rectF.left, rectF.top, rectF.right, rectF.bottom);
    }

    public final void d(float f10, float f11) {
        this.f11742a.lineTo(f10, f11);
    }

    public final boolean e(u0 u0Var, u0 u0Var2, int i10) {
        Path.Op op2;
        if (i10 == 0) {
            op2 = Path.Op.DIFFERENCE;
        } else if (i10 == 1) {
            op2 = Path.Op.INTERSECT;
        } else if (i10 == 4) {
            op2 = Path.Op.REVERSE_DIFFERENCE;
        } else {
            op2 = i10 == 2 ? Path.Op.UNION : Path.Op.XOR;
        }
        if (!(u0Var instanceof l)) {
            throw new UnsupportedOperationException("Unable to obtain android.graphics.Path");
        }
        Path path = ((l) u0Var).f11742a;
        if (u0Var2 instanceof l) {
            return this.f11742a.op(path, ((l) u0Var2).f11742a, op2);
        }
        throw new UnsupportedOperationException("Unable to obtain android.graphics.Path");
    }

    public final void f() {
        this.f11742a.reset();
    }
}
