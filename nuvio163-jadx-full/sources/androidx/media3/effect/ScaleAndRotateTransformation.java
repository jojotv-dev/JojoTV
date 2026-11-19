package androidx.media3.effect;

import android.graphics.Matrix;
import androidx.media3.common.util.Assertions;
import androidx.media3.common.util.Size;
import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public final class ScaleAndRotateTransformation implements MatrixTransformation {
    private Matrix adjustedTransformationMatrix;
    public final float rotationDegrees;
    public final float scaleX;
    public final float scaleY;
    private final Matrix transformationMatrix;

    public static final class Builder {
        private float scaleX = 1.0f;
        private float scaleY = 1.0f;
        private float rotationDegrees = 0.0f;

        public ScaleAndRotateTransformation build() {
            return new ScaleAndRotateTransformation(this.scaleX, this.scaleY, this.rotationDegrees);
        }

        public Builder setRotationDegrees(float f10) {
            float f11 = f10 % 360.0f;
            this.rotationDegrees = f11;
            if (f11 < 0.0f) {
                this.rotationDegrees = f11 + 360.0f;
            }
            return this;
        }

        public Builder setScale(float f10, float f11) {
            this.scaleX = f10;
            this.scaleY = f11;
            return this;
        }
    }

    @Override // androidx.media3.effect.GlMatrixTransformation
    public Size configure(int i10, int i11) {
        Assertions.checkArgument(i10 > 0, "inputWidth must be positive");
        Assertions.checkArgument(i11 > 0, "inputHeight must be positive");
        this.adjustedTransformationMatrix = new Matrix(this.transformationMatrix);
        if (this.transformationMatrix.isIdentity()) {
            return new Size(i10, i11);
        }
        float f10 = i10;
        float f11 = i11;
        float f12 = f10 / f11;
        this.adjustedTransformationMatrix.preScale(f12, 1.0f);
        this.adjustedTransformationMatrix.postScale(1.0f / f12, 1.0f);
        float[][] fArr = {new float[]{-1.0f, -1.0f, 0.0f, 1.0f}, new float[]{-1.0f, 1.0f, 0.0f, 1.0f}, new float[]{1.0f, -1.0f, 0.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f, 1.0f}};
        float fMax = Float.MIN_VALUE;
        float fMin = Float.MAX_VALUE;
        float fMin2 = Float.MAX_VALUE;
        float fMax2 = Float.MIN_VALUE;
        for (int i12 = 0; i12 < 4; i12++) {
            float[] fArr2 = fArr[i12];
            this.adjustedTransformationMatrix.mapPoints(fArr2);
            fMin = Math.min(fMin, fArr2[0]);
            fMax = Math.max(fMax, fArr2[0]);
            fMin2 = Math.min(fMin2, fArr2[1]);
            fMax2 = Math.max(fMax2, fArr2[1]);
        }
        float f13 = (fMax - fMin) / 2.0f;
        float f14 = (fMax2 - fMin2) / 2.0f;
        this.adjustedTransformationMatrix.postScale(1.0f / f13, 1.0f / f14);
        return new Size(Math.round(f10 * f13), Math.round(f11 * f14));
    }

    @Override // androidx.media3.effect.MatrixTransformation
    public Matrix getMatrix(long j10) {
        return (Matrix) Assertions.checkStateNotNull(this.adjustedTransformationMatrix, "configure must be called first");
    }

    @Override // androidx.media3.effect.GlEffect
    public boolean isNoOp(int i10, int i11) {
        Size sizeConfigure = configure(i10, i11);
        return ((Matrix) Assertions.checkStateNotNull(this.adjustedTransformationMatrix)).isIdentity() && i10 == sizeConfigure.getWidth() && i11 == sizeConfigure.getHeight();
    }

    private ScaleAndRotateTransformation(float f10, float f11, float f12) {
        this.scaleX = f10;
        this.scaleY = f11;
        this.rotationDegrees = f12;
        Matrix matrix = new Matrix();
        this.transformationMatrix = matrix;
        matrix.postScale(f10, f11);
        matrix.postRotate(f12);
    }
}
