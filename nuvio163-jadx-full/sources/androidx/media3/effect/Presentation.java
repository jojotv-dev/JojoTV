package androidx.media3.effect;

import android.graphics.Matrix;
import androidx.media3.common.C;
import androidx.media3.common.util.Assertions;
import androidx.media3.common.util.Size;
import androidx.media3.common.util.UnstableApi;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public final class Presentation implements MatrixTransformation {
    private static final float ASPECT_RATIO_UNSET = -1.0f;
    public static final int LAYOUT_SCALE_TO_FIT = 0;
    public static final int LAYOUT_SCALE_TO_FIT_WITH_CROP = 1;
    public static final int LAYOUT_STRETCH_TO_FIT = 2;
    private final int layout;
    private float outputHeight;
    private float outputWidth;
    private final boolean preservePortraitWhenApplicable;
    private float requestedAspectRatio;
    private final int requestedHeightPixels;
    private final int requestedWidthPixels;
    private final int textureMinFilter;
    private Matrix transformationMatrix;

    @Target({ElementType.TYPE_USE})
    @Documented
    @Retention(RetentionPolicy.SOURCE)
    public @interface Layout {
    }

    private Presentation(int i10, int i11, float f10, int i12, int i13, boolean z2) {
        Assertions.checkArgument(f10 == ASPECT_RATIO_UNSET || i10 == -1, "width and aspect ratio should not both be set");
        this.requestedWidthPixels = i10;
        this.requestedHeightPixels = i11;
        this.requestedAspectRatio = f10;
        this.layout = i12;
        this.textureMinFilter = i13;
        this.preservePortraitWhenApplicable = z2;
        this.outputWidth = ASPECT_RATIO_UNSET;
        this.outputHeight = ASPECT_RATIO_UNSET;
        this.transformationMatrix = new Matrix();
    }

    private void applyAspectRatio() {
        float f10 = this.outputWidth;
        float f11 = this.outputHeight;
        float f12 = f10 / f11;
        int i10 = this.layout;
        if (i10 == 0) {
            float f13 = this.requestedAspectRatio;
            if (f13 > f12) {
                this.transformationMatrix.setScale(f12 / f13, 1.0f);
                this.outputWidth = this.outputHeight * this.requestedAspectRatio;
                return;
            } else {
                this.transformationMatrix.setScale(1.0f, f13 / f12);
                this.outputHeight = this.outputWidth / this.requestedAspectRatio;
                return;
            }
        }
        if (i10 == 1) {
            float f14 = this.requestedAspectRatio;
            if (f14 > f12) {
                this.transformationMatrix.setScale(1.0f, f14 / f12);
                this.outputHeight = this.outputWidth / this.requestedAspectRatio;
                return;
            } else {
                this.transformationMatrix.setScale(f12 / f14, 1.0f);
                this.outputWidth = this.outputHeight * this.requestedAspectRatio;
                return;
            }
        }
        if (i10 == 2) {
            float f15 = this.requestedAspectRatio;
            if (f15 > f12) {
                this.outputWidth = f11 * f15;
            } else {
                this.outputHeight = f10 / f15;
            }
        }
    }

    private static void checkLayout(int i10) {
        boolean z2 = true;
        if (i10 != 0 && i10 != 1 && i10 != 2) {
            z2 = false;
        }
        Assertions.checkArgument(z2, "invalid layout " + i10);
    }

    public static Presentation createForAspectRatio(float f10, int i10) {
        Assertions.checkArgument(f10 > 0.0f, "aspect ratio " + f10 + " must be positive");
        checkLayout(i10);
        return new Presentation(-1, -1, f10, i10, C.TEXTURE_MIN_FILTER_LINEAR, false);
    }

    public static Presentation createForHeight(int i10) {
        return new Presentation(-1, i10, ASPECT_RATIO_UNSET, 0, C.TEXTURE_MIN_FILTER_LINEAR, false);
    }

    public static Presentation createForShortSide(int i10) {
        Assertions.checkArgument(i10 > 0, "shortSide " + i10 + " must be positive");
        return new Presentation(-1, i10, ASPECT_RATIO_UNSET, 0, C.TEXTURE_MIN_FILTER_LINEAR, true);
    }

    public static Presentation createForWidthAndHeight(int i10, int i11, int i12) {
        Assertions.checkArgument(i10 > 0, "width " + i10 + " must be positive");
        Assertions.checkArgument(i11 > 0, "height " + i11 + " must be positive");
        checkLayout(i12);
        return new Presentation(i10, i11, ASPECT_RATIO_UNSET, i12, C.TEXTURE_MIN_FILTER_LINEAR, false);
    }

    @Override // androidx.media3.effect.GlMatrixTransformation
    public Size configure(int i10, int i11) {
        int i12;
        Assertions.checkArgument(i10 > 0, "inputWidth must be positive");
        Assertions.checkArgument(i11 > 0, "inputHeight must be positive");
        this.transformationMatrix = new Matrix();
        this.outputWidth = i10;
        this.outputHeight = i11;
        int i13 = this.requestedWidthPixels;
        if (i13 != -1 && (i12 = this.requestedHeightPixels) != -1) {
            this.requestedAspectRatio = i13 / i12;
        }
        if (this.requestedAspectRatio != ASPECT_RATIO_UNSET) {
            applyAspectRatio();
        }
        int i14 = this.requestedHeightPixels;
        if (i14 != -1) {
            int i15 = this.requestedWidthPixels;
            if (i15 != -1) {
                this.outputWidth = i15;
                this.outputHeight = i14;
            } else if (!this.preservePortraitWhenApplicable || i11 <= i10) {
                this.outputWidth = (i14 * this.outputWidth) / this.outputHeight;
                this.outputHeight = i14;
            } else {
                this.outputHeight = (i14 * this.outputHeight) / this.outputWidth;
                this.outputWidth = i14;
            }
        }
        return new Size(Math.round(this.outputWidth), Math.round(this.outputHeight));
    }

    public Presentation copyWithTextureMinFilter(int i10) {
        Assertions.checkArgument(i10 == 9729 || i10 == 9987);
        return new Presentation(this.requestedWidthPixels, this.requestedHeightPixels, this.requestedAspectRatio, this.layout, i10, this.preservePortraitWhenApplicable);
    }

    @Override // androidx.media3.effect.GlMatrixTransformation
    public int getGlTextureMinFilter() {
        return this.textureMinFilter;
    }

    @Override // androidx.media3.effect.MatrixTransformation
    public Matrix getMatrix(long j10) {
        return (Matrix) Assertions.checkStateNotNull(this.transformationMatrix, "configure must be called first");
    }

    @Override // androidx.media3.effect.GlEffect
    public boolean isNoOp(int i10, int i11) {
        configure(i10, i11);
        return ((Matrix) Assertions.checkStateNotNull(this.transformationMatrix)).isIdentity() && i10 == Math.round(this.outputWidth) && i11 == Math.round(this.outputHeight);
    }
}
