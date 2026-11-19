package androidx.media3.effect;

import android.util.Pair;
import androidx.media3.common.OverlaySettings;
import androidx.media3.common.util.Assertions;
import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public final class StaticOverlaySettings implements OverlaySettings {
    private final float alphaScale;
    private final Pair<Float, Float> backgroundFrameAnchor;
    private final float hdrLuminanceMultiplier;
    private final Pair<Float, Float> overlayFrameAnchor;
    private final float rotationDegrees;
    private final Pair<Float, Float> scale;

    public static final class Builder {
        private float alphaScale = 1.0f;
        private Pair<Float, Float> backgroundFrameAnchor = OverlaySettings.DEFAULT_BACKGROUND_FRAME_ANCHOR;
        private Pair<Float, Float> overlayFrameAnchor = OverlaySettings.DEFAULT_OVERLAY_FRAME_ANCHOR;
        private Pair<Float, Float> scale = OverlaySettings.DEFAULT_SCALE;
        private float rotationDegrees = 0.0f;
        private float hdrLuminanceMultiplier = 1.0f;

        public StaticOverlaySettings build() {
            return new StaticOverlaySettings(this.alphaScale, this.backgroundFrameAnchor, this.overlayFrameAnchor, this.scale, this.rotationDegrees, this.hdrLuminanceMultiplier);
        }

        public Builder setAlphaScale(float f10) {
            Assertions.checkArgument(0.0f <= f10, "alphaScale needs to be greater than or equal to zero.");
            this.alphaScale = f10;
            return this;
        }

        public Builder setBackgroundFrameAnchor(float f10, float f11) {
            boolean z2 = false;
            Assertions.checkArgument(-1.0f <= f10 && f10 <= 1.0f);
            if (-1.0f <= f11 && f11 <= 1.0f) {
                z2 = true;
            }
            Assertions.checkArgument(z2);
            this.backgroundFrameAnchor = Pair.create(Float.valueOf(f10), Float.valueOf(f11));
            return this;
        }

        public Builder setHdrLuminanceMultiplier(float f10) {
            this.hdrLuminanceMultiplier = f10;
            return this;
        }

        public Builder setOverlayFrameAnchor(float f10, float f11) {
            boolean z2 = false;
            Assertions.checkArgument(-1.0f <= f10 && f10 <= 1.0f);
            if (-1.0f <= f11 && f11 <= 1.0f) {
                z2 = true;
            }
            Assertions.checkArgument(z2);
            this.overlayFrameAnchor = Pair.create(Float.valueOf(f10), Float.valueOf(f11));
            return this;
        }

        public Builder setRotationDegrees(float f10) {
            this.rotationDegrees = f10;
            return this;
        }

        public Builder setScale(float f10, float f11) {
            this.scale = Pair.create(Float.valueOf(f10), Float.valueOf(f11));
            return this;
        }
    }

    @Override // androidx.media3.common.OverlaySettings
    public float getAlphaScale() {
        return this.alphaScale;
    }

    @Override // androidx.media3.common.OverlaySettings
    public Pair<Float, Float> getBackgroundFrameAnchor() {
        return this.backgroundFrameAnchor;
    }

    @Override // androidx.media3.common.OverlaySettings
    public float getHdrLuminanceMultiplier() {
        return this.hdrLuminanceMultiplier;
    }

    @Override // androidx.media3.common.OverlaySettings
    public Pair<Float, Float> getOverlayFrameAnchor() {
        return this.overlayFrameAnchor;
    }

    @Override // androidx.media3.common.OverlaySettings
    public float getRotationDegrees() {
        return this.rotationDegrees;
    }

    @Override // androidx.media3.common.OverlaySettings
    public Pair<Float, Float> getScale() {
        return this.scale;
    }

    private StaticOverlaySettings(float f10, Pair<Float, Float> pair, Pair<Float, Float> pair2, Pair<Float, Float> pair3, float f11, float f12) {
        this.alphaScale = f10;
        this.backgroundFrameAnchor = pair;
        this.overlayFrameAnchor = pair2;
        this.scale = pair3;
        this.rotationDegrees = f11;
        this.hdrLuminanceMultiplier = f12;
    }
}
