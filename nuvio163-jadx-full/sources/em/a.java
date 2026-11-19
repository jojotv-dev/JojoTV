package em;

import a0.i;
import com.liskovsoft.sharedutils.helpers.PermissionHelpers;

/* JADX INFO: loaded from: classes5.dex */
public final class a {

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static final a f15785f = new a("qruov1q127L3v156m54-shake", 1, 127, 3, 156, 54);

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final a f15786g = new a("qruov1q31L3v165m60-shake", 1, 31, 3, 165, 60);

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public static final a f15787h = new a("qruov1q31L10v600m70-shake", 1, 31, 10, 600, 70);

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final a f15788i = new a("qruov1q7L10v740m100-shake", 1, 7, 10, 740, 100);

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public static final a f15789j = new a("qruov3q127L3v228m78-shake", 3, 127, 3, 228, 78);
    public static final a k = new a("qruov3q31L3v246m87-shake", 3, 31, 3, 246, 87);

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public static final a f15790l = new a("qruov3q31L10v890m100-shake", 3, 31, 10, 890, 100);

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final a f15791m = new a("qruov3q7L10v1100m140-shake", 3, 7, 10, 1100, 140);

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final a f15792n = new a("qruov5q127L3v306m105-shake", 5, 127, 3, 306, 105);

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public static final a f15793o = new a("qruov5q31L3v324m114-shake", 5, 31, 3, 324, PermissionHelpers.REQUEST_OVERLAY);

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public static final a f15794p = new a("qruov5q31L10v1120m120-shake", 5, 31, 10, 1120, 120);

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public static final a f15795q = new a("qruov5q7L10v1490m190-shake", 5, 7, 10, 1490, 190);

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f15796a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f15797b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f15798c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final int f15799d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int f15800e;

    static {
        new a("qruov1q127L3v156m54-aes", 1, 127, 3, 156, 54);
        new a("qruov1q31L3v165m60-aes", 1, 31, 3, 165, 60);
        new a("qruov1q31L10v600m70-aes", 1, 31, 10, 600, 70);
        new a("qruov1q7L10v740m100-aes", 1, 7, 10, 740, 100);
        new a("qruov3q127L3v228m78-aes", 3, 127, 3, 228, 78);
        new a("qruov3q31L3v246m87-aes", 3, 31, 3, 246, 87);
        new a("qruov3q31L10v890m100-aes", 3, 31, 10, 890, 100);
        new a("qruov3q7L10v1100m140-aes", 3, 7, 10, 1100, 140);
        new a("qruov5q127L3v306m105-aes", 5, 127, 3, 306, 105);
        new a("qruov5q31L3v324m114-aes", 5, 31, 3, 324, PermissionHelpers.REQUEST_OVERLAY);
        new a("qruov5q31L10v1120m120-aes", 5, 31, 10, 1120, 120);
        new a("qruov5q7L10v1490m190-aes", 5, 7, 10, 1490, 190);
    }

    public a(String str, int i10, int i11, int i12, int i13, int i14) {
        int i15;
        this.f15796a = str;
        if (i11 == 7) {
            this.f15797b = 3;
        } else if (i11 == 31) {
            this.f15797b = 5;
        } else {
            if (i11 != 127) {
                throw new IllegalArgumentException(i.k("unsupported q=", i11));
            }
            this.f15797b = 7;
        }
        if (i10 == 1) {
            i15 = 16;
        } else if (i10 == 3) {
            i15 = 24;
        } else {
            if (i10 != 5) {
                throw new IllegalArgumentException(i.k("unsupported security cat=", i10));
            }
            i15 = 32;
        }
        this.f15798c = i15;
        int i16 = this.f15798c;
        int i17 = i13 / i12;
        int i18 = i14 / i12;
        int i19 = (i13 + i14) / i12;
        this.f15799d = ((((((((i18 + 1) * i18) / 2) * i14) * i12) * this.f15797b) + 7) >>> 3) + i16;
        this.f15800e = i16 * 2;
    }
}
