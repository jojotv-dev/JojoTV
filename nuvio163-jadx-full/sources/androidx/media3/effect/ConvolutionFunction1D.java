package androidx.media3.effect;

import androidx.media3.common.util.Size;
import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public interface ConvolutionFunction1D {

    public interface Provider {
        Size configure(Size size);

        ConvolutionFunction1D getConvolution(long j10);
    }

    float domainEnd();

    float domainStart();

    float value(float f10);

    default float width() {
        return domainEnd() - domainStart();
    }
}
