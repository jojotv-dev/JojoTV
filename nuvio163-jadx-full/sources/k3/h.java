package k3;

import android.graphics.Paint;
import android.text.style.LineHeightSpan;

/* JADX INFO: loaded from: classes.dex */
public final class h implements LineHeightSpan {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final float f27706b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f27707c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final boolean f27708e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final boolean f27709i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final float f27710m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public final int f27711n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public int f27712o = Integer.MIN_VALUE;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public int f27713p = Integer.MIN_VALUE;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public int f27714q = Integer.MIN_VALUE;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public int f27715r = Integer.MIN_VALUE;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    public int f27716s;

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    public int f27717t;

    public h(float f10, int i10, boolean z2, boolean z10, float f11, int i11) {
        this.f27706b = f10;
        this.f27707c = i10;
        this.f27708e = z2;
        this.f27709i = z10;
        this.f27710m = f11;
        this.f27711n = i11;
        if ((0.0f > f11 || f11 > 1.0f) && f11 != -1.0f) {
            n3.a.c("topRatio should be in [0..1] range or -1");
        }
    }

    @Override // android.text.style.LineHeightSpan
    public final void chooseHeight(CharSequence charSequence, int i10, int i11, int i12, int i13, Paint.FontMetricsInt fontMetricsInt) {
        double dCeil;
        int i14 = fontMetricsInt.descent;
        int i15 = fontMetricsInt.ascent;
        if (i14 - i15 <= 0) {
            return;
        }
        boolean z2 = i10 == 0;
        boolean z10 = i11 == this.f27707c;
        int i16 = this.f27711n;
        boolean z11 = this.f27709i;
        boolean z12 = this.f27708e;
        if (z2 && z10 && z12 && z11 && i16 != 2) {
            return;
        }
        if (this.f27712o == Integer.MIN_VALUE) {
            int i17 = i14 - i15;
            int iCeil = (int) Math.ceil(this.f27706b);
            int i18 = iCeil - i17;
            if (i16 != 1 || i18 > 0) {
                float fAbs = this.f27710m;
                if (fAbs == -1.0f) {
                    fAbs = Math.abs(fontMetricsInt.ascent) / (fontMetricsInt.descent - fontMetricsInt.ascent);
                }
                if (i18 <= 0) {
                    dCeil = Math.ceil(i18 * fAbs);
                } else {
                    dCeil = Math.ceil((1.0f - fAbs) * i18);
                }
                int i19 = (int) dCeil;
                int i20 = fontMetricsInt.descent;
                int i21 = i19 + i20;
                this.f27714q = i21;
                int i22 = i21 - iCeil;
                this.f27713p = i22;
                if (i16 == 0 || i18 >= 0) {
                    if (z12) {
                        i22 = fontMetricsInt.ascent;
                    }
                    this.f27712o = i22;
                    if (z11) {
                        i21 = i20;
                    }
                    this.f27715r = i21;
                    this.f27716s = fontMetricsInt.ascent - i22;
                    this.f27717t = i21 - i20;
                } else if (i16 == 2) {
                    this.f27712o = z12 ? Math.max(fontMetricsInt.ascent, i22) : Math.min(fontMetricsInt.ascent, i22);
                    this.f27715r = z11 ? Math.min(fontMetricsInt.descent, this.f27714q) : Math.max(fontMetricsInt.descent, this.f27714q);
                    this.f27716s = 0;
                    this.f27717t = 0;
                }
            } else {
                int i23 = fontMetricsInt.ascent;
                this.f27713p = i23;
                int i24 = fontMetricsInt.descent;
                this.f27714q = i24;
                this.f27712o = i23;
                this.f27715r = i24;
                this.f27716s = 0;
                this.f27717t = 0;
            }
        }
        fontMetricsInt.ascent = z2 ? this.f27712o : this.f27713p;
        fontMetricsInt.descent = z10 ? this.f27715r : this.f27714q;
    }
}
