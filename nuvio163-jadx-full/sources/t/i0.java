package t;

import java.util.Arrays;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class i0 extends u {
    public i0(int i10) {
        this.f57068a = i10 == 0 ? y.f57100a : new long[i10];
    }

    public final void a(long j10) {
        int i10 = this.f57069b + 1;
        long[] jArr = this.f57068a;
        if (jArr.length < i10) {
            long[] jArrCopyOf = Arrays.copyOf(jArr, Math.max(i10, (jArr.length * 3) / 2));
            Intrinsics.checkNotNullExpressionValue(jArrCopyOf, "copyOf(...)");
            this.f57068a = jArrCopyOf;
        }
        long[] jArr2 = this.f57068a;
        int i11 = this.f57069b;
        jArr2[i11] = j10;
        this.f57069b = i11 + 1;
    }

    public /* synthetic */ i0() {
        this(16);
    }
}
