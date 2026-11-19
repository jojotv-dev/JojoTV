package nf;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class o0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f42433a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int[] f42434b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int[] f42435c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final int[] f42436d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int[] f42437e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final double[] f42438f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public final int[] f42439g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public final int[] f42440h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final byte[] f42441i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public final byte[] f42442j;
    public final byte[] k;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public final double[] f42443l;

    public o0(int i10, int i11, int[] centers) {
        int i12;
        Intrinsics.checkNotNullParameter(centers, "centers");
        int i13 = i10 + 1;
        this.f42433a = i13;
        this.f42434b = new int[i13];
        this.f42435c = new int[i13];
        this.f42436d = new int[i10 + 2];
        int[] iArr = new int[i13];
        for (int i14 = 0; i14 < i13; i14++) {
            iArr[i14] = Integer.MAX_VALUE;
        }
        this.f42437e = iArr;
        if (i10 >= 0) {
            int i15 = 0;
            i12 = 0;
            while (true) {
                int iMax = i15 == 0 ? 0 : Math.max(0, centers[i15] - 28);
                int iMin = Math.min(i11, centers[i15] + 28);
                this.f42434b[i15] = iMax;
                this.f42435c[i15] = iMin;
                this.f42436d[i15] = i12;
                i12 += (iMin - iMax) + 1;
                if (i15 == i10) {
                    break;
                } else {
                    i15++;
                }
            }
        } else {
            i12 = 0;
        }
        this.f42436d[this.f42433a] = i12;
        double[] dArr = new double[i12];
        for (int i16 = 0; i16 < i12; i16++) {
            dArr[i16] = Double.POSITIVE_INFINITY;
        }
        this.f42438f = dArr;
        int[] iArr2 = new int[i12];
        for (int i17 = 0; i17 < i12; i17++) {
            iArr2[i17] = -1;
        }
        this.f42439g = iArr2;
        int[] iArr3 = new int[i12];
        for (int i18 = 0; i18 < i12; i18++) {
            iArr3[i18] = -1;
        }
        this.f42440h = iArr3;
        this.f42441i = new byte[i12];
        this.f42442j = new byte[i12];
        this.k = new byte[i12];
        this.f42443l = new double[i12];
    }

    public final int a(int i10, int i11) {
        if (i10 >= 0 && i10 < this.f42433a) {
            int i12 = this.f42434b[i10];
            int i13 = this.f42435c[i10];
            if (i11 >= i12 && i11 <= i13) {
                return (this.f42436d[i10] + i11) - i12;
            }
        }
        return -1;
    }
}
