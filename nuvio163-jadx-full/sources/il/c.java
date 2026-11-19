package il;

import androidx.media3.extractor.ts.TsExtractor;
import java.util.Arrays;

/* JADX INFO: loaded from: classes5.dex */
public final class c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int[] f24222a;

    public c(int[] iArr) {
        this.f24222a = (int[]) iArr.clone();
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof c) {
            return Arrays.equals(this.f24222a, ((c) obj).f24222a);
        }
        return false;
    }

    public final int hashCode() {
        int[] iArr = this.f24222a;
        if (iArr == null) {
            return 0;
        }
        int length = iArr.length;
        int i10 = length + 1;
        while (true) {
            length--;
            if (length < 0) {
                return i10;
            }
            i10 = (i10 * TsExtractor.TS_STREAM_TYPE_AIT) ^ iArr[length];
        }
    }
}
