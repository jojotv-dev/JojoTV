package androidx.media3.extractor.mp4;

import androidx.media3.common.util.UnstableApi;
import androidx.media3.extractor.SniffFailure;
import java.util.Arrays;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public final class UnsupportedBrandsSniffFailure implements SniffFailure {
    public final u7.a compatibleBrands;
    public final int majorBrand;

    public UnsupportedBrandsSniffFailure(int i10, int[] iArr) {
        u7.a aVar;
        this.majorBrand = i10;
        if (iArr != null) {
            u7.a aVar2 = u7.a.f58283e;
            aVar = iArr.length == 0 ? u7.a.f58283e : new u7.a(Arrays.copyOf(iArr, iArr.length));
        } else {
            aVar = u7.a.f58283e;
        }
        this.compatibleBrands = aVar;
    }
}
