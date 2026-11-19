package androidx.media3.effect;

import android.graphics.Matrix;
import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public interface MatrixTransformation extends GlMatrixTransformation {
    @Override // androidx.media3.effect.GlMatrixTransformation
    default float[] getGlMatrixArray(long j10) {
        return MatrixUtils.getGlMatrixArray(getMatrix(j10));
    }

    Matrix getMatrix(long j10);
}
