package io.sentry;

import androidx.media3.extractor.AacUtil;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public enum t6 {
    LOW(0.8f, 50000, 10),
    MEDIUM(1.0f, 75000, 30),
    HIGH(1.0f, AacUtil.AAC_LC_MAX_RATE_BYTES_PER_SECOND, 50);

    public final int bitRate;
    public final int screenshotQuality;
    public final float sizeScale;

    t6(float f10, int i10, int i11) {
        this.sizeScale = f10;
        this.bitRate = i10;
        this.screenshotQuality = i11;
    }

    public String serializedName() {
        return name().toLowerCase(Locale.ROOT);
    }
}
