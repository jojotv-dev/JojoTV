package androidx.media3.effect;

import android.graphics.Matrix;
import androidx.media3.common.util.Assertions;
import androidx.media3.common.util.Size;
import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public final class Crop implements MatrixTransformation {
    private final float bottom;
    private final float left;
    private final float right;
    private final float top;
    private Matrix transformationMatrix;

    public Crop(float f10, float f11, float f12, float f13) {
        Assertions.checkArgument(f11 > f10, "right value " + f11 + " should be greater than left value " + f10);
        Assertions.checkArgument(f13 > f12, "top value " + f13 + " should be greater than bottom value " + f12);
        this.left = f10;
        this.right = f11;
        this.bottom = f12;
        this.top = f13;
        this.transformationMatrix = new Matrix();
    }

    @Override // androidx.media3.effect.GlMatrixTransformation
    public Size configure(int i10, int i11) {
        Assertions.checkArgument(i10 > 0, "inputWidth must be positive");
        Assertions.checkArgument(i11 > 0, "inputHeight must be positive");
        Matrix matrix = new Matrix();
        this.transformationMatrix = matrix;
        float f10 = this.left;
        if (f10 == -1.0f && this.right == 1.0f && this.bottom == -1.0f && this.top == 1.0f) {
            return new Size(i10, i11);
        }
        float f11 = this.right;
        float f12 = (f11 - f10) / 2.0f;
        float f13 = this.top;
        float f14 = this.bottom;
        float f15 = (f13 - f14) / 2.0f;
        matrix.postTranslate(-((f10 + f11) / 2.0f), -((f14 + f13) / 2.0f));
        this.transformationMatrix.postScale(1.0f / f12, 1.0f / f15);
        return new Size(Math.round(i10 * f12), Math.round(i11 * f15));
    }

    @Override // androidx.media3.effect.MatrixTransformation
    public Matrix getMatrix(long j10) {
        return (Matrix) Assertions.checkStateNotNull(this.transformationMatrix, "configure must be called first");
    }

    @Override // androidx.media3.effect.GlEffect
    public boolean isNoOp(int i10, int i11) {
        Size sizeConfigure = configure(i10, i11);
        return ((Matrix) Assertions.checkStateNotNull(this.transformationMatrix)).isIdentity() && i10 == sizeConfigure.getWidth() && i11 == sizeConfigure.getHeight();
    }
}
