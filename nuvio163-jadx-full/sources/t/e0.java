package t;

import java.util.Arrays;
import kotlin.collections.ArraysKt___ArraysJvmKt;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class e0 extends m {
    public e0(int i10) {
        this.f57029a = i10 == 0 ? r.f57057a : new int[i10];
    }

    public final void c(int i10) {
        d(this.f57030b + 1);
        int[] iArr = this.f57029a;
        int i11 = this.f57030b;
        iArr[i11] = i10;
        this.f57030b = i11 + 1;
    }

    public final void d(int i10) {
        int[] iArr = this.f57029a;
        if (iArr.length < i10) {
            int[] iArrCopyOf = Arrays.copyOf(iArr, Math.max(i10, (iArr.length * 3) / 2));
            Intrinsics.checkNotNullExpressionValue(iArrCopyOf, "copyOf(...)");
            this.f57029a = iArrCopyOf;
        }
    }

    public final void e(int i10) {
        int i11;
        if (i10 < 0 || i10 >= (i11 = this.f57030b)) {
            u.a.d("Index must be between 0 and size");
            throw null;
        }
        int[] iArr = this.f57029a;
        int i12 = iArr[i10];
        if (i10 != i11 - 1) {
            ArraysKt___ArraysJvmKt.copyInto(iArr, iArr, i10, i10 + 1, i11);
        }
        this.f57030b--;
    }

    public final void f(int i10, int i11) {
        if (i10 < 0 || i10 >= this.f57030b) {
            u.a.d("Index must be between 0 and size");
            throw null;
        }
        int[] iArr = this.f57029a;
        int i12 = iArr[i10];
        iArr[i10] = i11;
    }

    public /* synthetic */ e0() {
        this(16);
    }
}
