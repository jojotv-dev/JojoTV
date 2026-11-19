package g1;

import java.util.Arrays;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class t0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public int[] f18604a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f18605b;

    public t0() {
        this.f18604a = new int[10];
    }

    public int a(int i10) {
        int i11 = this.f18605b - 1;
        return i11 >= 0 ? this.f18604a[i11] : i10;
    }

    public int b() {
        int[] iArr = this.f18604a;
        int i10 = this.f18605b - 1;
        this.f18605b = i10;
        return iArr[i10];
    }

    public void c(int i10) {
        int[] iArrCopyOf = this.f18604a;
        if (this.f18605b >= iArrCopyOf.length) {
            iArrCopyOf = Arrays.copyOf(iArrCopyOf, iArrCopyOf.length * 2);
            Intrinsics.checkNotNullExpressionValue(iArrCopyOf, "copyOf(...)");
            this.f18604a = iArrCopyOf;
        }
        int i11 = this.f18605b;
        this.f18605b = i11 + 1;
        iArrCopyOf[i11] = i10;
    }

    public void d(int i10, int i11, int i12) {
        int i13 = this.f18605b;
        int[] iArrCopyOf = this.f18604a;
        int i14 = i13 + 3;
        if (i14 >= iArrCopyOf.length) {
            iArrCopyOf = Arrays.copyOf(iArrCopyOf, iArrCopyOf.length * 2);
            Intrinsics.checkNotNullExpressionValue(iArrCopyOf, "copyOf(...)");
            this.f18604a = iArrCopyOf;
        }
        iArrCopyOf[i13] = i10 + i12;
        iArrCopyOf[i13 + 1] = i11 + i12;
        iArrCopyOf[i13 + 2] = i12;
        this.f18605b = i14;
    }

    public void e(int i10, int i11, int i12, int i13) {
        int i14 = this.f18605b;
        int[] iArrCopyOf = this.f18604a;
        int i15 = i14 + 4;
        if (i15 >= iArrCopyOf.length) {
            iArrCopyOf = Arrays.copyOf(iArrCopyOf, iArrCopyOf.length * 2);
            Intrinsics.checkNotNullExpressionValue(iArrCopyOf, "copyOf(...)");
            this.f18604a = iArrCopyOf;
        }
        iArrCopyOf[i14] = i10;
        iArrCopyOf[i14 + 1] = i11;
        iArrCopyOf[i14 + 2] = i12;
        iArrCopyOf[i14 + 3] = i13;
        this.f18605b = i15;
    }

    public void f(int i10, int i11) {
        if (i10 < i11) {
            int i12 = i10 - 3;
            for (int i13 = i10; i13 < i11; i13 += 3) {
                int[] iArr = this.f18604a;
                int i14 = iArr[i13];
                int i15 = iArr[i11];
                if (i14 < i15 || (i14 == i15 && iArr[i13 + 1] <= iArr[i11 + 1])) {
                    i12 += 3;
                    g(i12, i13);
                }
            }
            g(i12 + 3, i11);
            f(i10, i12);
            f(i12 + 6, i11);
        }
    }

    public void g(int i10, int i11) {
        int[] iArr = this.f18604a;
        int i12 = iArr[i10];
        iArr[i10] = iArr[i11];
        iArr[i11] = i12;
        int i13 = i10 + 1;
        int i14 = i11 + 1;
        int i15 = iArr[i13];
        iArr[i13] = iArr[i14];
        iArr[i14] = i15;
        int i16 = i10 + 2;
        int i17 = i11 + 2;
        int i18 = iArr[i16];
        iArr[i16] = iArr[i17];
        iArr[i17] = i18;
    }

    public t0(int i10) {
        this.f18604a = new int[i10];
    }
}
