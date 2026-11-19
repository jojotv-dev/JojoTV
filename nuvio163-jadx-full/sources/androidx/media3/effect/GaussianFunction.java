package androidx.media3.effect;

import androidx.media3.common.util.Assertions;
import androidx.media3.common.util.UnstableApi;
import java.util.Objects;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public final class GaussianFunction implements ConvolutionFunction1D {
    private final float numStdDev;
    private final float sigma;

    public GaussianFunction(float f10, float f11) {
        Assertions.checkArgument(f10 > 0.0f && f11 > 0.0f);
        this.sigma = f10;
        this.numStdDev = f11;
    }

    @Override // androidx.media3.effect.ConvolutionFunction1D
    public float domainEnd() {
        return this.numStdDev * this.sigma;
    }

    @Override // androidx.media3.effect.ConvolutionFunction1D
    public float domainStart() {
        return (-this.numStdDev) * this.sigma;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GaussianFunction)) {
            return false;
        }
        GaussianFunction gaussianFunction = (GaussianFunction) obj;
        return Float.compare(gaussianFunction.sigma, this.sigma) == 0 && Float.compare(gaussianFunction.numStdDev, this.numStdDev) == 0;
    }

    public int hashCode() {
        return Objects.hash(Float.valueOf(this.sigma), Float.valueOf(this.numStdDev));
    }

    @Override // androidx.media3.effect.ConvolutionFunction1D
    public float value(float f10) {
        float fAbs = Math.abs(f10);
        float f11 = this.numStdDev;
        float f12 = this.sigma;
        if (fAbs > f11 * f12) {
            return 0.0f;
        }
        float f13 = f10 / f12;
        return (float) ((Math.exp(((-f13) * f13) / 2.0f) / Math.sqrt(6.283185307179586d)) / ((double) this.sigma));
    }
}
