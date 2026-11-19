package androidx.media3.effect;

import java.util.Objects;

/* JADX INFO: loaded from: classes3.dex */
final class ScaledLanczosFunction implements ConvolutionFunction1D {
    private final float radius;
    private final float scale;

    public ScaledLanczosFunction(float f10, float f11) {
        this.radius = f10;
        this.scale = f11;
    }

    @Override // androidx.media3.effect.ConvolutionFunction1D
    public float domainEnd() {
        return this.radius / this.scale;
    }

    @Override // androidx.media3.effect.ConvolutionFunction1D
    public float domainStart() {
        return (-this.radius) / this.scale;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ScaledLanczosFunction)) {
            return false;
        }
        ScaledLanczosFunction scaledLanczosFunction = (ScaledLanczosFunction) obj;
        return Float.compare(scaledLanczosFunction.radius, this.radius) == 0 && Float.compare(scaledLanczosFunction.scale, this.scale) == 0;
    }

    public int hashCode() {
        return Objects.hash(Float.valueOf(this.radius), Float.valueOf(this.scale));
    }

    @Override // androidx.media3.effect.ConvolutionFunction1D
    public float value(float f10) {
        float f11 = f10 * this.scale;
        if (Math.abs(f11) < 1.0E-5d) {
            return 1.0f;
        }
        float fAbs = Math.abs(f11);
        float f12 = this.radius;
        if (fAbs > f12) {
            return 0.0f;
        }
        double d10 = f11;
        double d11 = 3.141592653589793d * d10;
        return (float) ((Math.sin(d11 / ((double) this.radius)) * (Math.sin(d11) * ((double) f12))) / ((9.869604401089358d * d10) * d10));
    }
}
